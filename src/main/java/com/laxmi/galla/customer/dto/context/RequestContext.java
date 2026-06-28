package com.laxmi.galla.customer.dto.context;

import com.laxmi.galla.security.enums.Role;

public record RequestContext(
        Long userId,
        String email,
        Role role,
        boolean isSelfAction,
        String correlationId
) {}