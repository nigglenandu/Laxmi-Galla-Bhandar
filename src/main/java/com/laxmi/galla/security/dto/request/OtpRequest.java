package com.laxmi.galla.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Verify OTP request")
public record OtpRequest(

        @NotBlank(message = "Email is required")
        @Email
        @Schema(
                description = "Email address where OTP was sent",
                example = "nigglenandu@gmail.com"
        )
        String email,

        @NotBlank(message = "OTP is required")
        @Size(min = 6, max = 6, message = "OTP must be 6 digits")
        @Pattern(regexp = "\\d{6}", message = "OTP must contain only digits")
        @Schema(
                description = "6 digit OTP code sent to email",
                example = "123456"
        )
        String otp

) {}