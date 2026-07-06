package com.laxmi.galla.transaction.domain.event;

import java.time.Instant;
import java.util.UUID;

public record TransactionRestoredEvent(

        String transactionId,
        String restoredBy,
        String reason,
        Instant occurredAt,
        String correlationId

) {

    public static TransactionRestoredEvent of(
            String transactionId,
            String restoredBy,
            String reason
    ) {
        return new TransactionRestoredEvent(
                transactionId,
                restoredBy,
                reason,
                Instant.now(),
                UUID.randomUUID().toString()
        );
    }

    public static TransactionRestoredEvent of(
            String transactionId,
            String restoredBy,
            String reason,
            String correlationId
    ) {
        return new TransactionRestoredEvent(
                transactionId,
                restoredBy,
                reason,
                Instant.now(),
                correlationId != null
                        ? correlationId
                        : UUID.randomUUID().toString()
        );
    }
}