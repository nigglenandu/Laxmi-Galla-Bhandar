package com.laxmi.galla.customer.delete;

import java.time.Instant;
import java.util.UUID;

public record CustomerRestoredEvent(

        String customerId,
        String restoredBy,
        String reason,
        Instant occurredAt,
        String correlationId

) {

    public static CustomerRestoredEvent of(
            String customerId,
            String restoredBy,
            String reason
    ) {
        return new CustomerRestoredEvent(
                customerId,
                restoredBy,
                reason,
                Instant.now(),
                UUID.randomUUID().toString()
        );
    }

    public static CustomerRestoredEvent of(
            String customerId,
            String restoredBy,
            String reason,
            String correlationId
    ) {
        return new CustomerRestoredEvent(
                customerId,
                restoredBy,
                reason,
                Instant.now(),
                correlationId != null
                        ? correlationId
                        : UUID.randomUUID().toString()
        );
    }
}