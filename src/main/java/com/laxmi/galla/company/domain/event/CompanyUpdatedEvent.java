package com.laxmi.galla.company.domain.event;

import com.laxmi.galla.core.event.DomainEvent;

import java.time.Instant;

/**
 * Domain Event - Company Updated
 * Pure data carrier with observability and versioning.
 * Single clear factory entry point for consistency.
 */
public record CompanyUpdatedEvent(
        String customerId,
        Instant occurredAt,
        String correlationId,
        int version
) implements DomainEvent {

    /**
     * Main factory - only entry point.
     * Service layer decides correlationId and version.
     */
    public static CompanyUpdatedEvent of(String customerId, String correlationId) {
        return new CompanyUpdatedEvent(
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
        return CompanyEventTypes.COMPANY_UPDATED;
    }
}