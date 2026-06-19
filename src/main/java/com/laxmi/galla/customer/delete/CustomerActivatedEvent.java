package com.laxmi.galla.customer.delete;

import java.time.Instant;
import java.util.UUID;

public record CustomerActivatedEvent(

        String customerId,
        String activatedBy,
        String reason,
        Instant occurredAt,
        String correlationId

) {

    public static CustomerActivatedEvent of(
            String customerId,
            String activatedBy,
            String reason
    ) {
        return new CustomerActivatedEvent(
                customerId,
                activatedBy,
                reason,
                Instant.now(),
                UUID.randomUUID().toString()
        );
    }

    public static CustomerActivatedEvent of(
            String customerId,
            String activatedBy,
            String reason,
            String correlationId
    ) {
        return new CustomerActivatedEvent(
                customerId,
                activatedBy,
                reason,
                Instant.now(),
                correlationId != null
                        ? correlationId
                        : UUID.randomUUID().toString()
        );
    }
}