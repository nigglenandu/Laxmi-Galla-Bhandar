package com.laxmi.galla.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

//@PasswordMatch
@Schema(description = "Change password while authenticated")
public record ChangePasswordRequest(
    @NotBlank String currentPassword,
    @NotBlank
//    @ValidPassword
    String newPassword,
    @NotBlank String confirmPassword
) {}