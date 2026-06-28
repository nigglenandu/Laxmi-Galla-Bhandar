package com.laxmi.galla.customer.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event - Customer Blocked
 *
 * Used for:
 * - Audit logs
 * - Notifications
 * - Security monitoring
 * - Admin actions tracking
 */
public record CustomerBlockedEvent(

        String customerId,
        String blockedBy,
        String reason,
        Instant occurredAt,
        String correlationId

) {

    /**
     * Primary factory method (recommended)
     */
    public static CustomerBlockedEvent of(
            String customerId,
            String blockedBy,
            String reason
    ) {
        return new CustomerBlockedEvent(
                customerId,
                blockedBy,
                reason,
                Instant.now(),
                UUID.randomUUID().toString()
        );
    }

    /**
     * Overload when correlationId comes from RequestContext (MDC)
     */
    public static CustomerBlockedEvent of(
            String customerId,
            String blockedBy,
            String reason,
            String correlationId
    ) {
        return new CustomerBlockedEvent(
                customerId,
                blockedBy,
                reason,
                Instant.now(),
                correlationId != null ? correlationId : UUID.randomUUID().toString()
        );
    }
}