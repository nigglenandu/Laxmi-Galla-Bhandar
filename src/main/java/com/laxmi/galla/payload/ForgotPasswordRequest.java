package com.laxmi.galla.payload;

import jakarta.validation.constraints.NotNull;

public record ForgotPasswordRequest(
        @NotNull(message = "Email is required")
        String email
) {}
