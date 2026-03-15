package com.laxmi.galla.security.dto.response;

import com.laxmi.galla.security.entity.RoleEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Schema(description = "Authentication / Token refresh success response")
public record AuthResponse(
        String accessToken,
        String refreshToken,
        Instant refreshExpiresAt,
        long expiresInSeconds,
        Instant expiresAt,
        String tokenType,
        Instant issuedAt,
        String email,
        List<String> roles
) {

    public AuthResponse(String accessToken,
                        String refreshToken,
                        Instant refreshExpiresAt,
                        long expiresInSeconds,
                        String email,
                        List<String> roles) {
        this(
                accessToken,
                refreshToken,
                refreshExpiresAt,
                expiresInSeconds,
                Instant.now().plusSeconds(expiresInSeconds),
                "Bearer",
                Instant.now(),
                email,
                roles
        );
    }

}