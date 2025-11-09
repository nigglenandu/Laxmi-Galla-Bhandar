package com.laxmi.galla.JwtSecurity.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class SignupRequest {
    @NotNull(message = "Username is required")
    @Size(min = 3, max = 20, message = "username must be between 3 and 20 character")
    private String username;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Password is required")
    @Size(min = 6, message = "password must be at least 6 characters")
    private String password;

    private Set<String> roles;

    public @NotNull(message = "Email is required") @Email(message = "Invalid email format") String getEmail() {
        return email;
    }

    public void setEmail(@NotNull(message = "Email is required") @Email(message = "Invalid email format") String email) {
        this.email = email;
    }

    public @NotNull(message = "Password is required") @Size(min = 6, message = "password must be at least 6 characters") String getPassword() {
        return password;
    }

    public void setPassword(@NotNull(message = "Password is required") @Size(min = 6, message = "password must be at least 6 characters") String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public @NotNull(message = "Username is required") @Size(min = 3, max = 20, message = "username must be between 3 and 20 character") String getUsername() {
        return username;
    }

    public void setUsername(@NotNull(message = "Username is required") @Size(min = 3, max = 20, message = "username must be between 3 and 20 character") String username) {
        this.username = username;
    }
}
