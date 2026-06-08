package com.laxmi.galla.dto;

import java.time.LocalDateTime;

public record CustomerSearchCriteria(
        String searchTerm,
        Boolean active,
        Long categoryId,
        LocalDateTime createFrom,
        LocalDateTime createdTo
) {
}
