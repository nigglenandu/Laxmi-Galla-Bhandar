package core.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Base runtime exception for business/domain rule violations.
 *
 * - Maps to 4xx HTTP status (BAD_REQUEST, NOT_FOUND, CONFLICT, etc.)
 * - Carries errorCode, message, fieldErrors, and optional detail
 * - Immutable: fieldErrors defensively copied & unmodifiable
 * - Designed to be subclassed (e.g. ResourceNotFoundException)
 */
@Getter
@ToString(exclude = "fieldErrors")
@EqualsAndHashCode(exclude = "fieldErrors", callSuper = false)
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;
    private final Map<String, String> fieldErrors;
    private final String detail;  // ← NEW: optional internal/debug info (dev-only)

    // ────────────────────────────────────────────────────────────────
    // Full constructor (all fields explicit)
    // ────────────────────────────────────────────────────────────────

    public BusinessException(String message, String errorCode, HttpStatus status,
                             Map<String, String> fieldErrors, String detail) {
        super(message);
        this.errorCode = errorCode;
        this.status = (status != null) ? status : HttpStatus.BAD_REQUEST;
        this.fieldErrors = safeUnmodifiableCopy(fieldErrors);
        this.detail = detail;  // can be null
    }

    // ────────────────────────────────────────────────────────────────
    // Convenience overloads (chain to full constructor)
    // ────────────────────────────────────────────────────────────────

    public BusinessException(String message, String errorCode, HttpStatus status,
                             Map<String, String> fieldErrors) {
        this(message, errorCode, status, fieldErrors, null);
    }

    public BusinessException(String message, String errorCode, HttpStatus status) {
        this(message, errorCode, status, null, null);
    }

    public BusinessException(String message, String errorCode, Map<String, String> fieldErrors) {
        this(message, errorCode, HttpStatus.BAD_REQUEST, fieldErrors, null);
    }

    public BusinessException(String message, String errorCode) {
        this(message, errorCode, HttpStatus.BAD_REQUEST, null, null);
    }

    // ErrorCode overloads (same pattern)
    public BusinessException(String message, ErrorCode errorCode, Map<String, String> fieldErrors, String detail) {
        this(message, errorCode.getCode(), HttpStatus.BAD_REQUEST, fieldErrors, detail);
    }

    public BusinessException(String message, ErrorCode errorCode) {
        this(message, errorCode.getCode(), HttpStatus.BAD_REQUEST, null, null);
    }

    public BusinessException(String message, ErrorCode errorCode, HttpStatus status) {
        this(message, errorCode.getCode(), status, null, null);
    }

    public BusinessException(String message, ErrorCode errorCode, HttpStatus status, Map<String, String> fieldErrors) {
        this(message, errorCode.getCode(), status, fieldErrors, null);
    }

    // ────────────────────────────────────────────────────────────────
    // Helpers
    // ────────────────────────────────────────────────────────────────

    private static Map<String, String> safeUnmodifiableCopy(Map<String, String> source) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyMap();  // consistent non-null return
        }
        return Collections.unmodifiableMap(new HashMap<>(source));
    }

    /**
     * Returns field errors — never null (returns empty map when no errors)
     */
    public Map<String, String> getFieldErrors() {
        return fieldErrors != null ? fieldErrors : Collections.emptyMap();
    }

    // Getter for new field
    public String getDetail() {
        return detail;
    }
}