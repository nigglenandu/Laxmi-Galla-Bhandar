package com.laxmi.galla.transaction.domain.event;

import com.laxmi.galla.core.event.DomainEvent;

import java.time.Instant;

/**
 * Domain Event - Transaction Updated
 * Pure data carrier with observability and versioning.
 * Single clear factory entry point for consistency.
 */
public record TransactionUpdatedEvent(
        String transactionId,
        Instant occurredAt,
        String correlationId,
        int version
) implements DomainEvent {

    /**
     * Main factory - only entry point.
     * Service layer decides correlationId and version.
     */
    public static TransactionUpdatedEvent of(String transactionId, String correlationId) {
        return new TransactionUpdatedEvent(
                transactionId,
                Instant.now(),
                correlationId,
                1
        );
    }

    @Override
    public String aggregateId() {
        return transactionId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String eventType() {
        return TransactionEventTypes.CUSTOMER_UPDATED;
    }
}