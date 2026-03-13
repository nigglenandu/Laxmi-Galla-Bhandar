package com.laxmi.galla.dto;

import java.time.LocalDateTime;

public record CategoryResponseDto(
        Long id,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

