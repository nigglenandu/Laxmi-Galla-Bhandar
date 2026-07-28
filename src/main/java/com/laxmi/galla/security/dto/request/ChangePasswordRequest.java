package com.laxmi.galla.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Change password while authenticated")
public record ChangePasswordRequest(

        @NotBlank
        @Schema(
                description = "Current account password",
                example = "Password@123"
        )
        String currentPassword,

        @NotBlank
        @Schema(
                description = "New password",
                example = "Password@1234"
        )
//      @ValidPassword
        String newPassword,

        @NotBlank
        @Schema(
                description = "Confirm the new password",
                example = "Password@1234"
        )
        String confirmPassword
) {}