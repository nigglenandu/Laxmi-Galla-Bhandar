package com.laxmi.galla.customer.delete;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event - Customer Deleted
 * Clean, immutable, and production-ready.
 */
public record CustomerDeletedEvent(
        String customerId,
        String deletedBy,
        String reason,
        Instant occurredAt,
        String correlationId          // Keep for tracing
) {

    public static CustomerDeletedEvent of(String customerId, String deletedBy, String reason) {
        return new CustomerDeletedEvent(
                customerId,
                deletedBy,
                reason,
                Instant.now(),
                null
        );
    }
    /**
     * Overload for cases where correlationId is already known (e.g. from request).
     */
    public static CustomerDeletedEvent of(String customerId, String deletedBy, String reason, String correlationId) {
        return new CustomerDeletedEvent(
                customerId,
                deletedBy,
                reason,
                Instant.now(),
                correlationId != null ? correlationId : UUID.randomUUID().toString()
        );
    }

}