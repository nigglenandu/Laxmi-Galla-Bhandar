package com.laxmi.galla.security.dto.response;

import com.laxmi.galla.security.enums.OtpPurpose;

public record OtpVerificationResult(
        OtpPurpose purpose,

        AuthResponse authResponse
) {}