package core.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.dto.response.ApiResult;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

/**
 * Centralized 401 Unauthorized handler for JWT/stateless authentication.
 *
 * Production features:
 * - Consistent ApiResult shape
 * - Trace/correlation ID (MDC + fallback truncated UUID)
 * - i18n-ready message (MessageSource, cached internally)
 * - Micrometer metrics (counter + rich tags: reason, path, method, client IP)
 * - Detailed error only in dev profile
 * - Audit logging – structured key=value, sanitized path/IP
 * - Security headers (WWW-Authenticate – RFC 7235)
 * - Defensive null-checks & safe serialization
 */
@Component
@RequiredArgsConstructor
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger auditLogger = LoggerFactory.getLogger("security.audit");

    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;
    private final MeterRegistry meterRegistry;
    private final Environment environment;

    private static final String DEFAULT_UNAUTHORIZED_MSG =
            "Unauthorized – invalid or missing authentication token";

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        // Defensive null-checks (misconfiguration protection)
        if (objectMapper == null || meterRegistry == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server configuration error");
            return;
        }

        // Trace ID with fallback + truncation (8 chars for logs/metrics)
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString().substring(0, 8);
            MDC.put("traceId", traceId);
        } else if (traceId.length() > 8) {
            traceId = traceId.substring(0, 8);
        }

        String path = request.getRequestURI();
        String method = request.getMethod();
        String clientIp = getClientIp(request);

        // Audit log – structured, minimal info
        auditLogger.warn("Unauthorized access attempt | method={} | path={} | clientIp={} | traceId={} | reason={}",
                method, path, clientIp, traceId, authException.getMessage());

        // Metrics – rich tags for deep observability
        meterRegistry.counter("security.unauthorized.total",
                        "reason", authException.getClass().getSimpleName(),
                        "method", method,
                        "path", sanitizePath(path),
                        "clientIp", sanitizeIp(clientIp))
                .increment();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Security header – RFC 7235 compliant
        response.setHeader("WWW-Authenticate",
                "Bearer realm=\"api\", error=\"invalid_token\", error_description=\"Invalid or missing token\"");

        // i18n message lookup (cached internally by MessageSource)
        String message = messageSource.getMessage(
                "auth.unauthorized",
                null,
                DEFAULT_UNAUTHORIZED_MSG,
                LocaleContextHolder.getLocale()
        );

        // Build base error response
        ApiResult<Void> error = ApiResult.<Void>builder()
                .success(false)
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .errorCode("AUTH_UNAUTHORIZED")
                .message(message)
                .path(sanitizePath(path))
                .traceId(traceId)
                .timestamp(Instant.now())
                .build();

        // === Production-safe: NEVER expose internal details in prod ===
        if (environment.acceptsProfiles("dev", "local", "test")) {
            error = error.toBuilder()
                    .detail(authException.getMessage())
                    .build();
        }

        // Optional: private server-side logging of full details (prod only)
        if (!environment.acceptsProfiles("dev", "local", "test")) {
            auditLogger.error("401 Unauthorized - internal details (not sent to client) | traceId={} | path={} | reason={}",
                    traceId, path, authException.getMessage(), authException);
        }

        // Safe JSON write with fallback
        try {
            objectMapper.writeValue(response.getWriter(), error);
        } catch (Exception e) {
            auditLogger.error("Failed to serialize 401 response | traceId={} | error={}", traceId, e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "JSON serialization error");
        }
    }

    private String sanitizePath(String path) {
        if (path == null) return "unknown";
        int queryIndex = path.indexOf('?');
        return queryIndex > 0 ? path.substring(0, queryIndex) : path;
    }

    private String sanitizeIp(String ip) {
        if (ip == null || ip.isBlank()) return "unknown";
        int lastDot = ip.lastIndexOf('.');
        return lastDot > 0 ? ip.substring(0, lastDot + 1) + "xxx" : ip;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip != null ? ip : "unknown";
    }
}