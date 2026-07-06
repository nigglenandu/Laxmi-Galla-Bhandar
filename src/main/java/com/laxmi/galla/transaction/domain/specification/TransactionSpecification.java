package com.laxmi.galla.transaction.domain.specification;

import com.laxmi.galla.transaction.domain.entity.Transaction;
import com.laxmi.galla.transaction.dto.request.TransactionSearchCriteria;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TransactionSpecification {

    public static Specification<Transaction> withCriteria(TransactionSearchCriteria criteria) {

        Specification<Transaction> spec = (root, query, cb) -> cb.conjunction();

        if (criteria == null) return spec;

        // 1. Search term (description)
        if (criteria.searchTerm() != null && !criteria.searchTerm().isBlank()) {
            String pattern = "%" + criteria.searchTerm().toLowerCase().trim() + "%";

            spec = spec.and((root, query, cb) -> cb.like(
                    cb.lower(root.get("description")),
                    pattern
            ));
        }

        // 2. Company filter
        if (criteria.companyId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("company", JoinType.INNER).get("id"),
                            criteria.companyId())
            );
        }

        // 3. Unit filter
        if (criteria.unitId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("unit", JoinType.LEFT).get("id"),
                            criteria.unitId())
            );
        }

        // 4. Purchase / Sale filter
        if (criteria.purchaseOrSale() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("purchaseOrSale"),
                            criteria.purchaseOrSale())
            );
        }

        // 5. Date range filter
        if (criteria.fromDate() != null) {
            LocalDateTime from = criteria.fromDate().atStartOfDay();

            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("date"), from)
            );
        }

        if (criteria.toDate() != null) {
            LocalDateTime to = criteria.toDate().atTime(23, 59, 59);

            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("date"), to)
            );
        }

        // 6. Due amount filter (optional ledger use case)
        if (criteria.hasDueOnly() != null && criteria.hasDueOnly()) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThan(root.get("dueAmount"), 0)
            );
        }

        return spec;
    }
}