package com.laxmi.galla.payload;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull(message = "Username is required")
        @JsonAlias({"username", "email"})
        String usernameOrEmail,

        @NotNull(message = "Password is required")
        String password
) {}

