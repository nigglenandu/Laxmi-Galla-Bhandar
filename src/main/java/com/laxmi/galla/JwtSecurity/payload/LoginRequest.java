package com.laxmi.galla.JwtSecurity.payload;

import jakarta.validation.constraints.NotNull;

public class LoginRequest {
    @NotNull(message = "Username is required")
    private String username;

    @NotNull(message = "Password is required")
    private String password;

    public @NotNull(message = "Password is required") String getPassword() {
        return password;
    }

    public void setPassword(@NotNull(message = "Password is required") String password) {
        this.password = password;
    }

    public @NotNull(message = "Username is required") String getUsername() {
        return username;
    }

    public void setUsername(@NotNull(message = "Username is required") String username) {
        this.username = username;
    }
}
