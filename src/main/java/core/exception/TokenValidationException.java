package core.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Thrown when authentication token validation fails.
 * Common causes: expired, malformed, invalid signature, revoked, wrong audience/issuer, etc.
 * Maps to HTTP 401 Unauthorized by default.
 */
public class TokenValidationException extends BusinessException {

    private static final String DEFAULT_MESSAGE = "Invalid or expired authentication token";

    // ────────────────────────────────────────────────────────────────
    // Most common constructors — prefer enum
    // ────────────────────────────────────────────────────────────────

    public TokenValidationException() {
        this(DEFAULT_MESSAGE, ErrorCode.TOKEN_INVALID, HttpStatus.UNAUTHORIZED);
    }

    public TokenValidationException(String message) {
        this(message, ErrorCode.TOKEN_INVALID, HttpStatus.UNAUTHORIZED);
    }

    public TokenValidationException(ErrorCode errorCode) {
        this(DEFAULT_MESSAGE, errorCode, HttpStatus.UNAUTHORIZED);
    }

    // ────────────────────────────────────────────────────────────────
    // Convenient factory methods for frequent failure modes
    // ────────────────────────────────────────────────────────────────

    public static TokenValidationException expired() {
        return new TokenValidationException("Token has expired",
                ErrorCode.TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
    }

    public static TokenValidationException invalidSignature() {
        return new TokenValidationException("Invalid token signature",
                ErrorCode.TOKEN_INVALID_SIGNATURE, HttpStatus.UNAUTHORIZED);
    }

    public static TokenValidationException malformed() {
        return new TokenValidationException("Malformed or structurally invalid token",
                ErrorCode.TOKEN_MALFORMED, HttpStatus.UNAUTHORIZED);
    }

    public static TokenValidationException revoked() {
        return new TokenValidationException("Token has been revoked",
                ErrorCode.TOKEN_REVOKED, HttpStatus.UNAUTHORIZED);
    }

    // Optional — add more if your validation logic distinguishes these cases
    // public static TokenValidationException invalidAudience() { ... }

    // ────────────────────────────────────────────────────────────────
    // Full control constructors
    // ────────────────────────────────────────────────────────────────

    public TokenValidationException(String message, String errorCode, HttpStatus status) {
        super(message, errorCode, status);
    }

    public TokenValidationException(String message, ErrorCode errorCode, HttpStatus status) {
        super(message, errorCode, status);
    }

    // With field errors (uncommon for tokens, but possible)
    public TokenValidationException(String message, ErrorCode errorCode, HttpStatus status,
                                    Map<String, String> fieldErrors) {
        super(message, errorCode, status, fieldErrors);
    }

    // With internal detail (dev mode only)
    public TokenValidationException(String message, ErrorCode errorCode, HttpStatus status,
                                    Map<String, String> fieldErrors, String detail) {
        super(message, errorCode.getCode(), status, fieldErrors, detail);
    }
}