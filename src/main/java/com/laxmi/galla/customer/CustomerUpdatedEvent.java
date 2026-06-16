package com.laxmi.galla.customer;

import com.laxmi.galla.core.event.DomainEvent;
import com.laxmi.galla.customer.CustomerEventTypes;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event - Customer Updated
 * Pure data carrier with observability and versioning.
 * Single clear factory entry point for consistency.
 */
public record CustomerUpdatedEvent(
        String customerId,
        Instant occurredAt,
        String correlationId,
        int version
) implements DomainEvent {

    /**
     * Main factory - only entry point.
     * Service layer decides correlationId and version.
     */
    public static CustomerUpdatedEvent of(String customerId, String correlationId) {
        return new CustomerUpdatedEvent(
                customerId,
                Instant.now(),
                correlationId,
                1
        );
    }

    @Override
    public String aggregateId() {
        return customerId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String eventType() {
        return CustomerEventTypes.CUSTOMER_UPDATED;
    }
}