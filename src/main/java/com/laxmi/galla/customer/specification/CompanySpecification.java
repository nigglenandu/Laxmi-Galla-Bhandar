package com.laxmi.galla.customer.specification;

import com.laxmi.galla.company.domain.entity.Company;
import com.laxmi.galla.company.dto.request.CompanySearchCriteria;
import com.laxmi.galla.core.specification.BaseSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public final class CompanySpecification {

    private CompanySpecification() {}

    public static Specification<Company> withCriteria(CompanySearchCriteria criteria) {

        Specification<Company> spec =
                (root, query, cb) -> cb.conjunction();

        if (criteria == null) {
            return spec;
        }

        // Search by name, PAN, phone
        if (criteria.searchTerm() != null && !criteria.searchTerm().isBlank()) {

            String pattern = "%" + criteria.searchTerm().trim().toLowerCase() + "%";

            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("name")), pattern),
                            cb.like(cb.lower(root.get("panNumber")), pattern),
                            cb.like(cb.lower(root.get("phoneNo")), pattern)
                    )
            );
        }

        // Exact PAN filter
        if (criteria.panNumber() != null && !criteria.panNumber().isBlank()) {
            spec = spec.and(
                    BaseSpecification.fieldEquals("panNumber", criteria.panNumber())
            );
        }

        // Exact phone filter
        if (criteria.phoneNo() != null && !criteria.phoneNo().isBlank()) {
            spec = spec.and(
                    BaseSpecification.fieldEquals("phoneNo", criteria.phoneNo())
            );
        }

        // Created From
        if (criteria.createdFrom() != null) {
            Instant from = criteria.createdFrom();
            spec = spec.and(
                    BaseSpecification.fieldGreaterThan("createdAt", from)
            );
        }

        // Created To
        if (criteria.createdTo() != null) {
            Instant to = criteria.createdTo();
            spec = spec.and(
                    BaseSpecification.fieldLessThan("createdAt", to)
            );
        }

        return spec;
    }
}