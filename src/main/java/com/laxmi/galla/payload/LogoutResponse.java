package com.laxmi.galla.payload;

import org.springframework.http.ResponseCookie;

public record LogoutResponse(
        ResponseCookie clearAccessCookie,
        ResponseCookie clearRefreshCookie,
        String message
) {}