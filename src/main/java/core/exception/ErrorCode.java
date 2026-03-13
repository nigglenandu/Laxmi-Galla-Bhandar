package core.exception;

public enum ErrorCode {
    // Validation
    VALIDATION_FAILED("VALID_001"),
    PARAM_TYPE_MISMATCH("VALID_002"),

    // Authentication & Authorization
    AUTH_UNAUTHORIZED("AUTH_001"),
    AUTH_FORBIDDEN("AUTH_002"),

    // Upload / File
    UPLOAD_TOO_LARGE("UPLOAD_001"),

    // Server / Internal
    SERVER_INTERNAL("SRV_001"),
    SPRING_INTERNAL("SPRING_%d"),  // formatted with status code


    // Token-specific failures (most granular level)
    TOKEN_INVALID           ("TOKEN_001"),
    TOKEN_EXPIRED           ("TOKEN_002"),
    TOKEN_MALFORMED         ("TOKEN_003"),
    TOKEN_INVALID_SIGNATURE ("TOKEN_004"),
    TOKEN_REVOKED           ("TOKEN_005"),
    TOKEN_REUSED            ("TOKEN_006"),
    // TOKEN_INVALID_AUDIENCE  ("TOKEN_006"),   // optional — add if you validate 'aud' claim
    // TOKEN_INVALID_ISSUER     ("TOKEN_007"),   // optional
    // TOKEN_USED_BEFORE_NBF    ("TOKEN_008"),


    // Domain-specific (extend as needed)
    RESOURCE_NOT_FOUND("RES_001"),
    ENTITY_DELETED("RES_003"),
    DUPLICATE_RESOURCE("RES_002"),
    BUSINESS_RULE_VIOLATION("BUS_001");


    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Returns the formatted error code string.
     * Useful when the code contains placeholders (e.g. SPRING_%d).
     */
    public String getFormatted(Object... args) {
        return String.format(code, args);
    }
}