package com.laxmi.galla.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Reset password using OTP verification")
public record ResetForgotPasswordRequest(

        @NotBlank(message = "Email is required")
        @Email
        @Schema(
                description = "Registered email address",
                example = "nigglenandu@gmail.com"
        )
        String email,


        @NotBlank(message = "OTP is required")
        @Size(min = 6, max = 6, message = "OTP must be 6 digits")
        @Pattern(regexp = "\\d{6}", message = "OTP must contain only digits")
        @Schema(
                description = "6 digit OTP received on email",
                example = "123456"
        )
        String otp,


        @NotBlank
//      @ValidPassword
        @Schema(
                description = "New password",
                example = "Password@1234"
        )
        String newPassword,


        @NotBlank
        @Schema(
                description = "Confirm new password",
                example = "Password@1234"
        )
        String confirmPassword

) {}