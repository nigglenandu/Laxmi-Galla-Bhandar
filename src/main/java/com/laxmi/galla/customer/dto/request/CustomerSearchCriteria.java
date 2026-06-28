package com.laxmi.galla.customer.dto.request;

import java.time.Instant;

public record CustomerSearchCriteria(
        String searchTerm,
        Boolean active,
        Long categoryId,
        Instant createdFrom,
        Instant createdTo
) {
}
