package com.laxmi.galla.payload;

import java.util.List;

public record RefreshResponse(
        String accessToken,
        String refreshToken
) {}
