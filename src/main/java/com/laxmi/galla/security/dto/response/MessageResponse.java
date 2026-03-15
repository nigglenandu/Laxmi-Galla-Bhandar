package com.laxmi.galla.security.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Simple success/error message")
public record MessageResponse(
    String message,
    boolean success          // optional
) {}