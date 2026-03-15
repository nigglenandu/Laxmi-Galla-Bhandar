package com.laxmi.galla.security.dto.request;

public record RefreshRequest(
    String refreshToken   // optional if cookie-based
) {}