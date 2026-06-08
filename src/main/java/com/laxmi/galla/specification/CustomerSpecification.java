package com.laxmi.galla.specification;

import com.laxmi.galla.core.specification.BaseSpecification;
import com.laxmi.galla.dto.CustomerSearchCriteria;
import com.laxmi.galla.entity.CustomerEntity;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

/**
 * Customer Specification - Clean, composable, and production-ready.
 * Uses pure Specification chaining (Staff/Principal Level).
 */
public class CustomerSpecification {

    public static Specification<CustomerEntity> withCriteria(CustomerSearchCriteria criteria) {
        Specification<CustomerEntity> spec =
                (root, query, cb) -> cb.conjunction();
        String term = criteria.searchTerm();
        // 1. Multi-field search
        if (term != null && !term.isBlank()) {
            String pattern = "%" + term.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("firstName")), pattern),
                    cb.like(cb.lower(root.get("lastName")), pattern),
                    cb.like(cb.lower(root.get("panNumber")), pattern),
                    cb.like(cb.lower(root.get("address")), pattern)
            ));
        }

        // 2. Active / Deleted status
        if (criteria.active() != null) {
            if (Boolean.TRUE.equals(criteria.active())) {
                spec = spec.and(BaseSpecification.isActive());
            } else {
                spec = spec.and(BaseSpecification.isDeleted());
            }
        }

        // 3. Category filter (safe LEFT JOIN)
        if (criteria.categoryId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("categories", JoinType.LEFT).get("id"), criteria.categoryId())
            );
        }

        // 4. Date range (safe null handling)
        if (criteria.createdFrom() != null) {
            Instant from = criteria.createdFrom();
            spec = spec.and(BaseSpecification.fieldGreaterThan("createdAt", from));
        }

        if (criteria.createdTo() != null) {
            Instant to = criteria.createdTo();
            spec = spec.and(BaseSpecification.fieldLessThan("createdAt", to));
        }

        return spec;
    }
}