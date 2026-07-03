package com.laxmi.galla.company.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event - Company Deleted
 * Clean, immutable, and production-ready.
 */
public record CompanyDeletedEvent(
        String companyId,
        String deletedBy,
        String reason,
        Instant occurredAt,
        String correlationId          // Keep for tracing
) {

    public static CompanyDeletedEvent of(
            String companyId,
            String deletedBy,
            String reason
    ) {
        return of(companyId, deletedBy, reason, null);
    }

    /**
     * Overload for cases where correlationId is already known (e.g. from request).
     */
    public static CompanyDeletedEvent of(String companyId, String deletedBy, String reason, String correlationId) {
        return new CompanyDeletedEvent(
                companyId,
                deletedBy,
                reason,
                Instant.now(),
                correlationId != null ? correlationId : UUID.randomUUID().toString()
        );
    }

}