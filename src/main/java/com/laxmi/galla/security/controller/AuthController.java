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
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IAuthService authService;
    private final OtpService otpService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResult<Map<String, String>>> signup(
            @Valid @RequestBody SignupRequest signupRequest) {

        Map<String, String> data = authService.signup(signupRequest).getData();

        return ApiResult.<Map<String, String>>ok(data, "Signup successful")
                .toResponseEntity();
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResult<Void>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        OtpPurpose purpose = OtpPurpose.valueOf(request.purpose().toUpperCase());

        String traceId = MDC.get("traceId");
        System.out.printf("OTP verification requested | email=%s | purpose=%s | traceId=%s%n",
                request.email(), purpose, traceId);

        otpService.verifyOtp(request.email(), request.otp(), purpose);

        return ApiResult.<Void>ok(null, "OTP verified successfully")
                .toResponseEntity();
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResult<AuthResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        AuthResponse authResponse = authService.login(loginRequest, httpRequest, httpResponse).getData();

        return ApiResult.<AuthResponse>ok(authResponse, "Login successful")
                .toResponseEntity();
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResult<Void>> logout(HttpServletRequest request,
                                                          HttpServletResponse response,
                                                          Authentication auth){
        ApiResult<Void> result = authService.logout(request, response, auth);
        return result.toResponseEntity();
    }
}