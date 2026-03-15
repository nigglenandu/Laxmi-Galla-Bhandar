package com.laxmi.galla.core.exception;

import com.laxmi.galla.core.dto.response.ApiResult;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Global exception handler – production-hardened with observability & debug enhancements.
 *
 * Features:
 * - Consistent ApiResult shape across all errors
 * - Trace ID / correlation ID from MDC
 * - Dev mode shows internal details (detail field) – NEVER in prod
 * - Audit logging for security & business exceptions
 * - Rich metadata (path, method, timestamp, instance ID)
 * - Proper HTTP status + application error codes
 * - Handles all common Spring exceptions + custom ones
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final Environment env;

    private boolean isDevMode() {
        return env.acceptsProfiles("dev", "local", "development", "test");
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Custom Exceptions
    // ─────────────────────────────────────────────────────────────────────────────
    @ExceptionHandler(EntityDeletedException.class)
    public ResponseEntity<ApiResult<Void>> handleEntityDeleted(EntityDeletedException ex, WebRequest request) {
        log.warn("Entity deleted | traceId={} | path={}", getTraceId(), getRequestUri(request), ex);

        return ApiResult.<Void>builder()
                .success(false)
                .httpStatus(HttpStatus.GONE)           // 410 Gone – semantically better than 404
                .errorCode(ex.getErrorCode() != null ? ex.getErrorCode() : "ENTITY_DELETED")
                .message("The requested entity has been deleted")
                .path(getRequestUri(request))
                .traceId(getTraceId())
                .timestamp(Instant.now())
                .detail(isDevMode() ? ex.getMessage() : null)
                .build()
                .toResponseEntity();
    }

    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<ApiResult<Void>> handleTokenValidation(TokenValidationException ex, WebRequest request) {
        log.warn("Token validation failed | traceId={} | path={}", getTraceId(), getRequestUri(request), ex);

        return ApiResult.<Void>builder()
                .success(false)
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .errorCode("TOKEN_INVALID")
                .message("Invalid or expired authentication token")
                .path(getRequestUri(request))
                .traceId(getTraceId())
                .timestamp(Instant.now())
                .detail(isDevMode() ? ex.getMessage() : null)
                .build()
                .toResponseEntity();
    }
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResult<Void>> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        log.warn("User not found | traceId={} | path={}", getTraceId(), getRequestUri(request), ex);

        return ApiResult.<Void>builder()
                .success(false)
                .httpStatus(HttpStatus.NOT_FOUND)
                .errorCode(ex.getErrorCode())
                .message("User not found")
                .path(getRequestUri(request))
                .traceId(getTraceId())
                .timestamp(Instant.now())
                .detail(isDevMode() ? ex.getMessage() : null)
                .build()
                .toResponseEntity();
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Overridden Spring Handlers (return ResponseEntity<Object>)
    // ─────────────────────────────────────────────────────────────────────────────

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {

        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : "Invalid value"
                ));

        List<String> globalMsgs = ex.getBindingResult().getGlobalErrors().stream()
                .map(err -> err.getDefaultMessage())
                .filter(msg -> msg != null && !msg.isBlank())
                .collect(Collectors.toList());

        if (!globalMsgs.isEmpty()) {
            fieldErrors.put("__global__", String.join("; ", globalMsgs));
        }

        ApiResult<Void> body = ApiResult.<Void>builder()
                .success(false)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message("Validation failed")
                .errorCode("VALIDATION_FAILED")
                .fieldErrors(fieldErrors)
                .path(getRequestUri(request))
                .traceId(getTraceId())
                .timestamp(Instant.now())
                .detail(isDevMode() ? ex.getMessage() : null)
                .build();

        return ResponseEntity.status(statusCode).headers(headers).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {

        String msg = "Required request parameter '" + ex.getParameterName() + "' is missing";

        ApiResult<Void> body = ApiResult.badRequest(msg, "MISSING_PARAM")
                .withPath(getRequestUri(request))
                .withTraceId(getTraceId())
                .withDetail(isDevMode() ? ex.getMessage() : null);

        return ResponseEntity.status(statusCode).headers(headers).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {

        String supported = ex.getSupportedMediaTypes().stream()
                .map(MediaType::toString)
                .collect(Collectors.joining(", "));

        ApiResult<Void> body = ApiResult.error(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Unsupported media type. Supported: " + supported,
                "MEDIA_TYPE_NOT_SUPPORTED",
                null,
                null,
                getRequestUri(request),
                isDevMode() ? ex.getMessage() : null
        );

        return ResponseEntity.status(statusCode).headers(headers).body(body);
    }

    // No @Override – this is a custom handler
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {

        ApiResult<Void> body = ApiResult.<Void>builder()
                .success(false)
                .httpStatus(HttpStatus.NOT_FOUND)
                .errorCode("RESOURCE_NOT_FOUND")
                .message("Resource not found")
                .path(getRequestUri(request))
                .traceId(getTraceId())
                .timestamp(Instant.now())
                .detail(isDevMode() ? ex.getMessage() : null)
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).body(body);
    }
    // ─────────────────────────────────────────────────────────────────────────────
    // Custom @ExceptionHandler (return ApiResult<Void>)
    // ─────────────────────────────────────────────────────────────────────────────

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResult<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {

        String msg = String.format("Invalid parameter '%s' – expected type %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        return ApiResult.badRequest(msg, "PARAM_TYPE_MISMATCH")
                .withPath(getRequestUri(request))
                .withTraceId(getTraceId())
                .withDetail(isDevMode() ? ex.getMessage() : null)
                .toResponseEntity();
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResult<Void>> handleAuthentication(
            AuthenticationException ex, WebRequest request) {

        log.warn("Authentication failed | traceId={} | path={}", getTraceId(), getRequestUri(request), ex);

        return ApiResult.error(
                HttpStatus.UNAUTHORIZED,
                "Authentication required",
                "AUTH_UNAUTHORIZED",
                null,
                null,
                getRequestUri(request),
                isDevMode() ? ex.getMessage() : null
        ).toResponseEntity();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResult<Void>> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {

        log.warn("Access denied | traceId={} | path={}", getTraceId(), getRequestUri(request), ex);

        return ApiResult.error(
                HttpStatus.FORBIDDEN,
                "Insufficient permissions",
                "AUTH_FORBIDDEN",
                null,
                null,
                getRequestUri(request),
                isDevMode() ? ex.getMessage() : null
        ).toResponseEntity();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResult<Void>> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(v ->
                errors.put(v.getPropertyPath().toString(), v.getMessage())
        );

        log.warn("Constraint violation | traceId={} | path={}", getTraceId(), getRequestUri(request));

        return ApiResult.validationError(errors)
                .withPath(getRequestUri(request))
                .withTraceId(getTraceId())
                .withDetail(isDevMode() ? ex.getMessage() : null)
                .toResponseEntity();
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {

        if (ex instanceof MaxUploadSizeExceededException maxEx) {
            String maxSize = env.getProperty("spring.servlet.multipart.max-file-size", "unknown");
            Map<String, Object> meta = buildBaseMetadata(request);
            meta.put("maxAllowedSize", maxSize);

            ApiResult<Void> apiBody = ApiResult.<Void>builder()
                    .success(false)
                    .httpStatus(HttpStatus.PAYLOAD_TOO_LARGE)
                    .message("File size exceeds maximum limit")
                    .errorCode("UPLOAD_TOO_LARGE")
                    .metadata(meta)
                    .path(getRequestUri(request))
                    .traceId(getTraceId())
                    .timestamp(Instant.now())
                    .detail(isDevMode() ? ex.getMessage() : null)
                    .build();

            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).headers(headers).body(apiBody);
        }

        // fallback to default
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResult<Void>> handleBusinessException(
            BusinessException ex, WebRequest request) {

        log.info("Business rule violation | traceId={} | path={} | code={} | message={}",
                getTraceId(), getRequestUri(request), ex.getErrorCode(), ex.getMessage());

        return ApiResult.<Void>builder()
                .success(false)
                .httpStatus(ex.getStatus())
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .fieldErrors(ex.getFieldErrors())
                .path(getRequestUri(request))
                .traceId(getTraceId())
                .timestamp(Instant.now())
                .detail(isDevMode() ? ex.getDetail() : null)
                .build()
                .toResponseEntity();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Void>> handleAll(Exception ex, WebRequest request) {
        String instanceId = UUID.randomUUID().toString().substring(0, 12);
        String traceId = getTraceId();

        log.error("Unhandled exception | instanceId={} | traceId={} | path={} | method={}",
                instanceId, traceId, getRequestUri(request), getMethod(request), ex);

        Map<String, Object> meta = buildBaseMetadata(request);
        meta.put("errorInstanceId", instanceId);

        if (isDevMode()) {
            meta.put("debugException", ex.getClass().getName());
            meta.put("debugMessage", ex.getMessage());
        }

        return ApiResult.error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred – please contact support",
                "SERVER_INTERNAL",
                meta,
                null,
                getRequestUri(request),
                isDevMode() ? ex.getMessage() : null
        ).toResponseEntity();
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────────

    private Map<String, Object> buildBaseMetadata(WebRequest request) {
        Map<String, Object> meta = new HashMap<>();
        meta.put("traceId", getTraceId());
        meta.put("timestamp", Instant.now().toString());

        if (request instanceof ServletWebRequest swr) {
            meta.put("path", swr.getRequest().getRequestURI());
            meta.put("method", swr.getRequest().getMethod());
        }

        return meta;
    }

    private String getTraceId() {
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = MDC.get("X-B3-TraceId");
        }
        return traceId != null && !traceId.isBlank() ? traceId : "no-trace-" + System.currentTimeMillis();
    }

    private String getRequestUri(WebRequest request) {
        return (request instanceof ServletWebRequest swr) ? swr.getRequest().getRequestURI() : "unknown";
    }

    private String getMethod(WebRequest request) {
        return (request instanceof ServletWebRequest swr) ? swr.getRequest().getMethod() : "unknown";
    }
}

