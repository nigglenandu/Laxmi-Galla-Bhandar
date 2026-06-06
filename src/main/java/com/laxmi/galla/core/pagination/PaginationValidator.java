package com.laxmi.galla.core.pagination;

/**
 * All business validation rules.
 */
public final class PaginationValidator {

    private PaginationValidator() {}

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

    public static void validateCursor(int pageSize) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize must be greater than 0");
        }
    }
}