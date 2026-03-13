package com.laxmi.galla.payload;

import jakarta.validation.constraints.NotNull;

public record LogoutRequest(
        @NotNull(message = "Token is required")
        String token
) {}



