package com.laxmi.galla.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Verify OTP request")
public record OtpVerifyRequest(

        @Email
        @NotBlank
        @Schema(
                description = "Email address where OTP was sent",
                example = "nigglenandu@gmail.com"
        )
        String email,

        @NotBlank
        @Schema(
                description = "6 digit OTP code",
                example = "123456"
        )
        String otp,

        @NotBlank
        @Schema(
                description = "Purpose of OTP verification",
                example = "SIGNUP_VERIFICATION",
                allowableValues = {
                        "SIGNUP_VERIFICATION",
                        "LOGIN",
                        "ADMIN_LOGIN",
                        "PASSWORD_RESET"
                }
        )
        String purpose

) {}