package com.laxmi.galla.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

//@PasswordMatch
@Schema(description = "Reset password with OTP")
public record ResetForgotPasswordRequest(
        @NotBlank(message = "Email is required")
        @Email String email,

        @NotBlank(message = "OTP is required")
        @Size(min = 6, max = 6, message = "OTP must be 6 digits")
        @Pattern(regexp = "\\d{6}", message = "OTP must contain only digits")
        String otp,

    @NotBlank
//        @ValidPassword
        String newPassword,
    @NotBlank String confirmPassword
) {}