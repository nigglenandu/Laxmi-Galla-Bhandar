package com.laxmi.galla.core.pagination;

/**
 * Centralized validation for pagination.
 */
public final class PaginationValidator {

    private PaginationValidator() {}

    public static void validate(int currentPage, int pageSize, Integer totalPages,
                                PageResponse.PaginationMode mode) {

        if (mode == null) {
            throw new IllegalArgumentException("Pagination mode must not be null");
        }

        if (mode == PageResponse.PaginationMode.OFFSET) {
            validateOffset(currentPage, pageSize, totalPages);
        } else if (mode == PageResponse.PaginationMode.CURSOR) {
            validateCursor(pageSize, currentPage, totalPages);
        }
    }

    public static void validateOffset(int currentPage, int pageSize, Integer totalPages) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize must be greater than 0");
        }
        if (currentPage < 1) {
            throw new IllegalArgumentException("currentPage must be >= 1 in OFFSET mode");
        }
        if (totalPages != null && totalPages < 0) {
            throw new IllegalArgumentException("totalPages cannot be negative");
        }
        if (totalPages != null && totalPages > 0 && currentPage > totalPages) {
            throw new IllegalArgumentException(
                    String.format("currentPage (%d) cannot exceed totalPages (%d)", currentPage, totalPages));
        }
    }

    public static void validateCursor(int pageSize, Integer currentPage, Integer totalPages) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize must be greater than 0");
        }
        if (currentPage != null || totalPages != null) {
            throw new IllegalArgumentException("currentPage and totalPages must be null in CURSOR mode");
        }
    }
}