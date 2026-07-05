package com.laxmi.galla.customer.dto.response;

import com.laxmi.galla.categories.dto.response.CategoryResponseDto;

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