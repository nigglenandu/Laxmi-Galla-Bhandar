package com.laxmi.galla.company.domain.event;

import java.time.Instant;
import java.util.UUID;

public record CompanyRestoredEvent(

        String companyId,
        String restoredBy,
        String reason,
        Instant occurredAt,
        String correlationId

) {

    public static CompanyRestoredEvent of(
            String companyId,
            String restoredBy,
            String reason
    ) {
        return new CompanyRestoredEvent(
                companyId,
                restoredBy,
                reason,
                Instant.now(),
                UUID.randomUUID().toString()
        );
    }

    public static CompanyRestoredEvent of(
            String companyId,
            String restoredBy,
            String reason,
            String correlationId
    ) {
        return new CompanyRestoredEvent(
                companyId,
                restoredBy,
                reason,
                Instant.now(),
                correlationId != null
                        ? correlationId
                        : UUID.randomUUID().toString()
        );
    }
}