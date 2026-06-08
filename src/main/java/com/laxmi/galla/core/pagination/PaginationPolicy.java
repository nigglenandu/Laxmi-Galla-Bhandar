package com.laxmi.galla.core.pagination;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Lightweight Pagination Policy - Production Grade
 * Enforces security, limits, and consistency while respecting Spring Pageable.
 */
@Component
@Slf4j
public class PaginationPolicy {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 200;

    // Whitelisted sortable fields (security + performance)
    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "createdAt", "updatedAt", "name", "email", "phone", "status", "active");

    private static final Sort DEFAULT_SORT = Sort.by("id").descending();

    /**
     * Applies safe pagination rules and returns a consistent Pageable.
     */
    public Pageable apply(Pageable pageable) {
        if (pageable == null) {
            log.debug("Null Pageable received. Using defaults.");
            return PageRequest.of(0, DEFAULT_PAGE_SIZE, DEFAULT_SORT);
        }

        int safeSize = normalizeSize(pageable.getPageSize());
        Sort safeSort = sanitizeSort(pageable.getSort());

        Pageable safePageable = PageRequest.of(
                Math.max(pageable.getPageNumber(), 0),  // Spring is 0-based - keep as is
                safeSize,
                safeSort
        );

        // Optional observability (very useful in production)
        if (log.isDebugEnabled()) {
            log.debug("PaginationPolicy applied: page={}, size={}, sort={}", 
                    safePageable.getPageNumber(), safePageable.getPageSize(), safePageable.getSort());
        }

        return safePageable;
    }

    private int normalizeSize(int size) {
        return Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    }

    /**
     * Defensive sort sanitization.
     */
    private Sort sanitizeSort(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return DEFAULT_SORT;
        }

        List<Sort.Order> safeOrders = sort.stream()
                .filter(order -> ALLOWED_SORT_FIELDS.contains(order.getProperty().toLowerCase()))
                .toList();

        return safeOrders.isEmpty() ? DEFAULT_SORT : Sort.by(safeOrders);
    }

    // ────────────────────────────────────────────────────────────────
    // Configuration & Observability
    // ────────────────────────────────────────────────────────────────

    public int getMaxPageSize() {
        return MAX_PAGE_SIZE;
    }

    public Set<String> getAllowedSortFields() {
        return ALLOWED_SORT_FIELDS;
    }
}