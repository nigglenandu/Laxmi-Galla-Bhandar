package com.laxmi.galla.security.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

//
//@Schema(description = "Initiate password reset")
public record ForgotPasswordRequest(
    @NotBlank @Email String email
) {}