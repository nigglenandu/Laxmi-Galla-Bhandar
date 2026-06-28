package com.laxmi.galla.customer.domain.event;

import java.time.Instant;
import java.util.UUID;

public record CustomerDeactivatedEvent(

        String customerId,
        String deactivatedBy,
        String reason,
        Instant occurredAt,
        String correlationId

) {

    public static CustomerDeactivatedEvent of(
            String customerId,
            String deactivatedBy,
            String reason
    ) {
        return new CustomerDeactivatedEvent(
                customerId,
                deactivatedBy,
                reason,
                Instant.now(),
                UUID.randomUUID().toString()
        );
    }

    public static CustomerDeactivatedEvent of(
            String customerId,
            String deactivatedBy,
            String reason,
            String correlationId
    ) {
        return new CustomerDeactivatedEvent(
                customerId,
                deactivatedBy,
                reason,
                Instant.now(),
                correlationId != null
                        ? correlationId
                        : UUID.randomUUID().toString()
        );
    }
}