package com.laxmi.galla.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record CustomerResponseDto(
        Long id,
        String name,
        String contact,
        String address,
        String panNo,
        Set<CategoryResponseDto> categories,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean isDeleted
) {}

