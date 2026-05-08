package com.laxmi.galla.dto.response;

import java.time.Instant;
import java.util.Set;

public record CustomerResponseDto(

        String id,

        String firstName,

        String lastName,

        String fullName,

        String address,

        String panNumber,

        Set<CategoryResponseDto> categories,

        Instant createdAt,

        Instant updatedAt,

        boolean deleted

) {
}