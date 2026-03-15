package com.laxmi.galla.core.config;

import com.laxmi.galla.core.dto.response.ApiResult;
import lombok.NonNull;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.UUID;

/**
 * ResponseBodyAdvice that automatically injects the current traceId (correlation ID)
 * into every ApiResult<T> returned from controllers.
 *
 * - Only skips Actuator endpoints to prevent interference with health/metrics.
 * - Does NOT auto-wrap plain objects (controllers should explicitly return ApiResult).
 * - Uses MDC values populated by Micrometer Tracing / Brave / OpenTelemetry.
 */
@Component
public class ApiResultWrapperAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(@NonNull MethodParameter returnType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        // Only skip Actuator endpoints (health, metrics, env, prometheus, etc.)
        return !returnType.getContainingClass()
                .getName()
                .startsWith("org.springframework.boot.actuate");
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response) {

        // Retrieve traceId from MDC (set automatically by tracing instrumentation)
        String traceId = MDC.get("traceId");

        // Fallbacks for different tracing implementations
        if (traceId == null) {
            traceId = MDC.get("X-B3-TraceId"); // Common Brave / Zipkin legacy key
        }
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString(); // Strong, unique fallback
        }

        // Enhance ApiResult instances with traceId (immutable operation)
        if (body instanceof ApiResult<?> ApiResult) {
            return ApiResult.withTraceId(traceId);
        }

        // No automatic wrapping of non-ApiResult returns
        // → This keeps the API contract explicit and predictable
        //   (controllers must deliberately return ApiResult.ok(...), .error(...), etc.)
        return body;
    }
}