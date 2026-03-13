package com.laxmi.galla.dto;

import java.time.LocalDateTime;

public record CompanyResponseDto(
        Long id,
        String name,
        String panNo,
        String companyAddress,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
