package com.laxmi.galla.core.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Production-grade request/response logging filter.
 * Features:
 * - Correlation/trace ID via MDC (from header or generated)
 * - Request + response timing & status
 * - Sanitized client IP
 * - Payload logging (limited size, dev/test only or on errors)
 * - Bounded caching for request (to prevent OOM)
 * - Proper MDC cleanup
 * - Skips health/actuator/static paths
 */
@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_MDC_KEY = "traceId";
    private static final String REQUEST_ID_MDC_KEY = "requestId";

    private static final int REQUEST_BODY_CACHE_LIMIT = 256 * 1024;   // 256 KB – protects against large uploads
    private static final int MAX_PAYLOAD_LOG_CHARS = 2048;

    private static final Set<String> SKIPPED_PATH_PREFIXES = Set.of(
            "/actuator/", "/health", "/metrics", "/favicon.ico", "/static/", "/error"
    );

    private final Environment environment;

    public RequestLoggingFilter(Environment environment) {
        this.environment = environment;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path != null && SKIPPED_PATH_PREFIXES.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest rawRequest,
                                    HttpServletResponse rawResponse,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Instant start = Instant.now();

        var wrappedRequest = new ContentCachingRequestWrapper(rawRequest, REQUEST_BODY_CACHE_LIMIT);
        var wrappedResponse = new ContentCachingResponseWrapper(rawResponse);  // ← only 1 arg!

        String traceId = initTraceId(wrappedRequest);
        String requestId = UUID.randomUUID().toString().substring(0, 8);

        MDC.put(TRACE_ID_MDC_KEY, traceId);
        MDC.put(REQUEST_ID_MDC_KEY, requestId);
        MDC.put("method", wrappedRequest.getMethod());
        MDC.put("uri", sanitizeUri(wrappedRequest.getRequestURI()));
        MDC.put("clientIp", getSanitizedClientIp(wrappedRequest));

        try {
            logRequestStart(wrappedRequest);

            filterChain.doFilter(wrappedRequest, wrappedResponse);

            long durationMs = Duration.between(start, Instant.now()).toMillis();

            MDC.put("status", String.valueOf(wrappedResponse.getStatus()));
            MDC.put("durationMs", String.valueOf(durationMs));

            logRequestFinish(wrappedRequest, wrappedResponse, durationMs);

            wrappedResponse.copyBodyToResponse();  // Critical: copies cached body back to client

        } catch (Exception e) {
            log.error("Request failed | traceId={} | uri={}", traceId, wrappedRequest.getRequestURI(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    private String initTraceId(HttpServletRequest request) {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.trim().isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }
        return traceId;
    }

    private void logRequestStart(ContentCachingRequestWrapper request) {
        if (!log.isDebugEnabled() && !isDevLikeProfile()) {
            log.info("→ {} {}", request.getMethod(), request.getRequestURI());
            return;
        }

        String payload = safeGetRequestPayload(request);
        String query = request.getQueryString();

        log.info("→ Request | method={} | uri={} | query={} | payload={}",
                request.getMethod(),
                request.getRequestURI(),
                query != null ? query : "<none>",
                payload.isEmpty() ? "<empty>" : payload);
    }

    private void logRequestFinish(ContentCachingRequestWrapper request,
                                  ContentCachingResponseWrapper response,
                                  long durationMs) {

        String payload = safeGetResponsePayload(response);
        int status = response.getStatus();

        if (status >= 400) {
            log.warn("← Response | status={} | duration={}ms | payload={}",
                    status, durationMs, payload);
        } else if (log.isDebugEnabled() || isDevLikeProfile()) {
            log.debug("← Response | status={} | duration={}ms | payload={}",
                    status, durationMs, payload);
        } else {
            log.info("← Response | status={} | duration={}ms", status, durationMs);
        }
    }

    private String safeGetRequestPayload(ContentCachingRequestWrapper request) {
        try {
            byte[] buf = request.getContentAsByteArray();
            if (buf.length == 0) {
                return "";
            }

            Charset charset = Optional.ofNullable(request.getCharacterEncoding())
                    .filter(enc -> !enc.isBlank())
                    .map(Charset::forName)
                    .orElse(StandardCharsets.UTF_8);

            String payload = new String(buf, charset);
            return truncateIfNeeded(payload);
        } catch (Exception e) {
            return "<unreadable>";
        }
    }

    private String safeGetResponsePayload(ContentCachingResponseWrapper response) {
        try {
            byte[] buf = response.getContentAsByteArray();
            if (buf.length == 0) {
                return "";
            }

            String contentType = response.getContentType();
            if (contentType == null || !contentType.toLowerCase().contains(MediaType.APPLICATION_JSON_VALUE)) {
                return "<non-json or binary>";
            }

            String payload = new String(buf, StandardCharsets.UTF_8);
            return truncateIfNeeded(payload);
        } catch (Exception e) {
            return "<unreadable>";
        }
    }

    private String truncateIfNeeded(String text) {
        return text.length() > MAX_PAYLOAD_LOG_CHARS
                ? text.substring(0, MAX_PAYLOAD_LOG_CHARS) + "… [truncated]"
                : text;
    }

    private String sanitizeUri(String uri) {
        if (uri == null) return "null";
        int queryIndex = uri.indexOf('?');
        return queryIndex >= 0 ? uri.substring(0, queryIndex) : uri;
    }

    private String getSanitizedClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip == null || ip.isBlank()) {
            return "unknown";
        }

        int lastDot = ip.lastIndexOf('.');
        return lastDot > 0 ? ip.substring(0, lastDot + 1) + "xxx" : ip;
    }

    private boolean isDevLikeProfile() {
        return environment.acceptsProfiles("dev", "local", "test", "integration");
    }
}