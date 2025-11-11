package com.laxmi.galla.JwtSecurity.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ResetPasswordResponse {

    @NotNull(message = "Token is required")
    private String token;

    @NotNull(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;
}
