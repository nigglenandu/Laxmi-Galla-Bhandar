package com.laxmi.galla.dto;

import java.util.Set;

public record CustomerRequestDto(
        String name,
        String contact,
        String address,
        String panNo,
        Set<Long> categoryIds
) {}
