package com.laxmi.galla.core.dto.response;

public record TokenPair(
        String accessToken,
        String refreshToken
) {}