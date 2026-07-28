package com.laxmi.galla.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Refresh access token request")
public record RefreshRequest(

        @Schema(
                description = "Refresh token. Optional when using HttpOnly cookie authentication.",
                example = "eyJhbGciOiJIUzI1NiJ9..."
        )
        String refreshToken

) {}