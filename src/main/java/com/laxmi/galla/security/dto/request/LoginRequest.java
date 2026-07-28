package com.laxmi.galla.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Login credentials")
public record LoginRequest(

        @NotBlank
        @Email
        @Schema(
                description = "Registered user email address",
                example = "nigglenandu@gmail.com"
        )
        String email,

        @NotBlank
        @Size(min = 8)
        @Schema(
                description = "User password",
                example = "Password@123"
        )
        String password

) {}