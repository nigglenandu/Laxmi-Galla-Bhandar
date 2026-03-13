package com.laxmi.galla.payload;

import java.util.List;

public record JwtResponse(
        String accessToken,
        String refreshToken,
        String type,
        Long userId,
        String username,
        String email,
        List<String> roles
) {
    public JwtResponse(String accessToken,
                       String refreshToken,
                       List<String> roles,
                       Long userId,
                       String username,
                       String email) {
        this(accessToken, refreshToken, "Bearer", userId, username, email, roles);
    }
}
