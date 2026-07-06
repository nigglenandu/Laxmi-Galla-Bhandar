package com.laxmi.galla.transaction.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event - Transaction Deleted
 * Clean, immutable, and production-ready.
 */
public record TransactionDeletedEvent(
        String transactionId,
        String deletedBy,
        String reason,
        Instant occurredAt,
        String correlationId          // Keep for tracing
) {

    public static TransactionDeletedEvent of(
            String transactionId,
            String deletedBy,
            String reason
    ) {
        return of(transactionId, deletedBy, reason, null);
    }

    /**
     * Overload for cases where correlationId is already known (e.g. from request).
     */
    public static TransactionDeletedEvent of(String transactionId, String deletedBy, String reason, String correlationId) {
        return new TransactionDeletedEvent(
                transactionId,
                deletedBy,
                reason,
                Instant.now(),
                correlationId != null ? correlationId : UUID.randomUUID().toString()
        );
    }

}