package com.laxmi.galla.security.controller;

import com.laxmi.galla.core.dto.response.ApiResult;
import com.laxmi.galla.security.dto.request.LoginRequest;
import com.laxmi.galla.security.dto.request.OtpVerifyRequest;
import com.laxmi.galla.security.dto.request.SignupRequest;
import com.laxmi.galla.security.dto.response.AuthResponse;
import com.laxmi.galla.security.enums.OtpPurpose;
import com.laxmi.galla.security.service.IAuthService;
import com.laxmi.galla.security.service.OtpService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final IAuthService authService;
    private final OtpService otpService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<Map<String, String>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return authService.signup(signupRequest);
    }

    @PostMapping("/verify")
    public ApiResult<Void> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        OtpPurpose purpose;
        try {
            purpose = OtpPurpose.valueOf(request.purpose().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ApiResult.badRequest("Invalid OTP purpose", "OTP_INVALID_PURPOSE");
        }

        otpService.verifyOtp(request.email(), request.otp(), purpose);

        return ApiResult.ok(null, "OTP verified successfully");
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        return authService.login(loginRequest, request, response);
    }


}
