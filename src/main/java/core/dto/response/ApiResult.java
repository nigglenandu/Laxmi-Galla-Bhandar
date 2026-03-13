package core.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Standardized, immutable API response wrapper for all endpoints.
 *
 * - Explicit success flag
 * - HTTP status + application error code
 * - Trace/correlation ID for observability
 * - Timestamp (automatic)
 * - Clean JSON (NON_NULL)
 * - Optional dev-only detail field (never populated in prod)
 */
@Data
@Builder(toBuilder = true)          // allows .toBuilder() for safe modifications
@NoArgsConstructor(force = true)    // for Jackson safety
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final boolean success;
    private final String message;
    private final T data;

    @Builder.Default
    private final Instant timestamp = Instant.now();

    private final HttpStatus httpStatus;

    // Application-level error code (e.g. "AUTH_UNAUTHORIZED", "VALIDATION_FAILED")
    private final String errorCode;

    // Trace / correlation ID – populated from MDC in ResponseBodyAdvice or filter
    @Builder.Default
    private final String traceId = UUID.randomUUID().toString();  // fallback

    // Extensible: pagination, links, facets, request context, etc.
    private final Map<String, Object> metadata;

    // Field-level validation errors (e.g. {"email":"Invalid format"})
    private final Map<String, String> fieldErrors;

    private final String path;   // ← the request URI/path (for debugging & observability)

    // ── Dev-only: internal debug info (NEVER populated in production) ────────
    private final String detail;   // ← only set in dev/test/local profiles

    // ── Success Factories ──────────────────────────────────────────────────────

    public static <T> ApiResult<T> ok(T data) {
        return ApiResult.<T>builder()
                .success(true)
                .httpStatus(HttpStatus.OK)
                .data(data)
                .message("Operation successful")
                .build();
    }

    public static <T> ApiResult<T> created(T data) {
        return ApiResult.<T>builder()
                .success(true)
                .httpStatus(HttpStatus.CREATED)
                .data(data)
                .message("Resource created successfully")
                .build();
    }

    public static ApiResult<Void> noContent() {
        return ApiResult.<Void>builder()
                .success(true)
                .httpStatus(HttpStatus.NO_CONTENT)
                .message("No content")
                .build();
    }

    public static <T> ApiResult<T> ok(T data, String message) {
        return ok(data).toBuilder().message(message).build();
    }

    // ── Error Factories ────────────────────────────────────────────────────────

    public static ApiResult<Void> badRequest(String message, String errorCode) {
        return error(HttpStatus.BAD_REQUEST, message, errorCode, null, null, null, null);
    }

    public static ApiResult<Void> validationError(Map<String, String> fieldErrors) {
        return ApiResult.<Void>builder()
                .success(false)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message("Validation failed")
                .fieldErrors(fieldErrors)
                .errorCode("VALIDATION_FAILED")
                .build();
    }

    public static ApiResult<Void> notFound(String message, String errorCode) {
        return error(HttpStatus.NOT_FOUND, message, errorCode, null, null, null, null);
    }

    public static ApiResult<Void> unauthorized(String message, String errorCode) {
        return error(HttpStatus.UNAUTHORIZED, message, errorCode, null, null, null, null);
    }

    public static ApiResult<Void> error(HttpStatus status, String message, String errorCode,
                                        Map<String, Object> metadata, Map<String, String> fieldErrors,
                                        String path, String detail) {
        return ApiResult.<Void>builder()
                .success(false)
                .httpStatus(status)
                .message(message != null ? message : status.getReasonPhrase())
                .errorCode(errorCode)
                .metadata(metadata)
                .fieldErrors(fieldErrors)
                .path(path)
                .detail(detail)
                .build();
    }

    // Convenience method to add path
    public ApiResult<T> withPath(String path) {
        return this.toBuilder().path(path).build();
    }
    // ── Utility / Conversion Methods ───────────────────────────────────────────

    public ResponseEntity<ApiResult<T>> toResponseEntity() {
        return new ResponseEntity<>(this, this.httpStatus);
    }

    public ApiResult<T> withTraceId(String traceId) {
        return this.toBuilder().traceId(traceId).build();
    }

    public ApiResult<T> withMetadata(Map<String, Object> metadata) {
        return this.toBuilder().metadata(metadata).build();
    }

    public ApiResult<T> withDetail(String detail) {
        return this.toBuilder().detail(detail).build();
    }
}