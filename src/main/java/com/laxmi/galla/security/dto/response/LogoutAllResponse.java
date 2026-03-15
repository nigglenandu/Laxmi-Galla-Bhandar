package com.laxmi.galla.security.dto.response;

public record LogoutAllResponse(
    String message,
    int devicesLoggedOut
) {}