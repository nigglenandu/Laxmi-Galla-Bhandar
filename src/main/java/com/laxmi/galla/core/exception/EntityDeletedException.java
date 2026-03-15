package com.laxmi.galla.core.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Thrown when attempting to access or operate on an entity that has been (soft-)deleted.
 * <p>
 * This exception signals that the resource once existed but is no longer available
 * (e.g., soft-deleted, archived, or permanently removed).
 * </p>
 * <p>
 * Default mapping: HTTP 410 Gone — preferred over 404 for resources that were intentionally removed.
 * </p>
 */
public class EntityDeletedException extends BusinessException {

    private static final String DEFAULT_MESSAGE = "The requested entity has been deleted";

    // ────────────────────────────────────────────────────────────────
    // Constructors – most common cases first
    // ────────────────────────────────────────────────────────────────

    /**
     * Default constructor using the standard message and dedicated error code.
     */
    public EntityDeletedException() {
        this(DEFAULT_MESSAGE, ErrorCode.ENTITY_DELETED, HttpStatus.GONE);
    }

    /**
     * Custom message, but still uses the standard ENTITY_DELETED code.
     */
    public EntityDeletedException(String message) {
        this(message, ErrorCode.ENTITY_DELETED, HttpStatus.GONE);
    }

    /**
     * Custom message + custom error code (rare – prefer enum when possible).
     */
    public EntityDeletedException(String message, String errorCode) {
        this(message, errorCode, HttpStatus.GONE);
    }

    /**
     * Using a specific ErrorCode enum value (allows flexibility while encouraging enum usage).
     */
    public EntityDeletedException(ErrorCode errorCode) {
        this(DEFAULT_MESSAGE, errorCode, HttpStatus.GONE);
    }

    // ────────────────────────────────────────────────────────────────
    // Full control constructors
    // ────────────────────────────────────────────────────────────────

    public EntityDeletedException(String message, String errorCode, HttpStatus status) {
        super(message, errorCode, status);
    }

    public EntityDeletedException(String message, ErrorCode errorCode, HttpStatus status) {
        super(message, errorCode, status);
    }

    /**
     * Constructor with field-level validation errors (uncommon for deleted-entity cases,
     * but supported for consistency with BusinessException).
     */
    public EntityDeletedException(String message, String errorCode, HttpStatus status,
                                  Map<String, String> fieldErrors) {
        super(message, errorCode, status, fieldErrors);
    }

    /**
     * Full constructor including optional internal detail (only shown in dev mode).
     */
    public EntityDeletedException(String message, String errorCode, HttpStatus status,
                                  Map<String, String> fieldErrors, String detail) {
        super(message, errorCode, status, fieldErrors, detail);
    }
}