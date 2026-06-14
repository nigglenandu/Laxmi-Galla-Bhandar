package com.laxmi.galla.customer.delete;

import java.time.Instant;

public record CustomerDeletedEvent(
        String customerId,
        String deletedBy,
        String reason,
        Instant occurredAt,
        String correlationId
) {}
