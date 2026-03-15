package com.laxmi.galla.core.specification;

import com.laxmi.galla.core.model.AuditableEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

/**
 * Powerful, reusable, dynamic, production-ready specifications for all auditable entities.
 * Supports:
 * - Active / deleted filtering
 * - User-based filtering (createdBy / updatedBy / deletedBy)
 * - Date ranges
 * - Dynamic combination of filters
 * - Future-proof: can extend to any entity field
 */
public class BaseSpecification {

    // ────────────────────────────────
    // Soft-delete filtering
    // ────────────────────────────────

    /** Active entities (deletedAt IS NULL) */
    public static <T extends AuditableEntity<?>> Specification<T> isActive() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    /** Deleted entities (deletedAt IS NOT NULL) */
    public static <T extends AuditableEntity<?>> Specification<T> isDeleted() {
        return (root, query, cb) -> cb.isNotNull(root.get("deletedAt"));
    }

    // ────────────────────────────────
    // User-based filtering
    // ────────────────────────────────

    /** Created by specific user */
    public static <T extends AuditableEntity<U>, U> Specification<T> createdBy(U user) {
        return (root, query, cb) -> cb.equal(root.get("createdBy"), user);
    }

    /** Updated by specific user */
    public static <T extends AuditableEntity<U>, U> Specification<T> updatedBy(U user) {
        return (root, query, cb) -> cb.equal(root.get("updatedBy"), user);
    }

    /** Deleted by specific user */
    public static <T extends AuditableEntity<U>, U> Specification<T> deletedBy(U user) {
        return (root, query, cb) -> cb.equal(root.get("deletedBy"), user);
    }

    // ────────────────────────────────
    // Date filtering
    // ────────────────────────────────

    /** Created between two instants */
    public static <T extends AuditableEntity<?>> Specification<T> createdBetween(Instant from, Instant to) {
        return (root, query, cb) -> cb.between(root.get("createdAt"), from, to);
    }

    /** Updated between two instants */
    public static <T extends AuditableEntity<?>> Specification<T> updatedBetween(Instant from, Instant to) {
        return (root, query, cb) -> cb.between(root.get("updatedAt"), from, to);
    }

    /** Deleted between two instants */
    public static <T extends AuditableEntity<?>> Specification<T> deletedBetween(Instant from, Instant to) {
        return (root, query, cb) -> cb.between(root.get("deletedAt"), from, to);
    }

    // ────────────────────────────────
    // Generic field filtering (future-proof)
    // ────────────────────────────────

    /**
     * Filter by any field dynamically
     */
    public static <T> Specification<T> fieldEquals(String fieldName, Object value) {
        return (root, query, cb) -> cb.equal(root.get(fieldName), value);
    }

    /**
     * Filter by field like pattern (contains)
     */
    public static <T> Specification<T> fieldContains(String fieldName, String value) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(fieldName)), "%" + value.toLowerCase() + "%");
    }

    /**
     * Greater than for numeric or date fields
     */
    public static <T, Y extends Comparable<? super Y>> Specification<T> fieldGreaterThan(String fieldName, Y value) {
        return (root, query, cb) -> cb.greaterThan(root.get(fieldName), value);
    }

    /**
     * Less than for numeric or date fields
     */
    public static <T, Y extends Comparable<? super Y>> Specification<T> fieldLessThan(String fieldName, Y value) {
        return (root, query, cb) -> cb.lessThan(root.get(fieldName), value);
    }

}