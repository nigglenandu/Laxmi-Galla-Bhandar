package com.laxmi.galla.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResetForgotPasswordRequest(

        @NotNull(message = "Email is required")
        String email,

        @NotNull(message = "OTP is required")
        String otp,

        @NotNull(message = "New password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String newPassword,

        @NotNull(message = "Confirm password is required")
        String confirmPassword
) {}
