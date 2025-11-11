package com.laxmi.galla.JwtSecurity.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ForgotPasswordRequest {

    @NotNull(message = "Email is required")
    @Size(message = "Invalid email format")
    private String email;

    public @NotNull(message = "Email is required") @Size(message = "Invalid email format") String getEmail() {
        return email;
    }

    public void setEmail(@NotNull(message = "Email is required") @Size(message = "Invalid email format") String email) {
        this.email = email;
    }
}
