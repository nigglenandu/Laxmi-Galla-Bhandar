package com.laxmi.galla.security.controller;

import com.laxmi.galla.core.dto.response.ApiResult;
import com.laxmi.galla.security.dto.request.LoginRequest;
import com.laxmi.galla.security.dto.request.OtpVerifyRequest;
import com.laxmi.galla.security.dto.request.SignupRequest;
import com.laxmi.galla.security.dto.response.AuthResponse;
import com.laxmi.galla.security.dto.response.OtpVerificationResult;
import com.laxmi.galla.security.enums.OtpPurpose;
import com.laxmi.galla.security.service.IAuthService;
import com.laxmi.galla.security.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth Controller", description = "APIs for user authentication and registration")
public class AuthController {

    private final IAuthService authService;
    private final OtpService otpService;

    @PostMapping("/signup")
    @Operation(summary = "User Signup", description = "Registers a new user. Sends OTP if email is not verified.")
    public ResponseEntity<ApiResult<Map<String, String>>> signup(
            @Valid @RequestBody SignupRequest signupRequest) {

        Map<String, String> data = authService.signup(signupRequest).getData();

        return ApiResult.<Map<String, String>>ok(data, "Signup successful")
                .toResponseEntity();
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify OTP", description = "Verifies OTP for verification or login completion")
    public ResponseEntity<? extends ApiResult<?>> verifyOtp(
            @Valid @RequestBody OtpVerifyRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        OtpVerificationResult result =
                authService.verifyOtp(request, httpRequest, httpResponse);

        if (result.purpose() == OtpPurpose.SIGNUP_VERIFICATION) {
            return ApiResult.<Void>ok(null, "Email verified successfully")
                    .toResponseEntity();
        }

        return ApiResult.<AuthResponse>ok(result.authResponse(), "Login successful")
                .toResponseEntity();
    }

    @PostMapping("/login")
    @Operation(summary = "User Logout", description = "Logout user and invalidate session/token")
    public ResponseEntity<ApiResult<Void>> login(
            @Valid @RequestBody LoginRequest loginRequest) {

        ApiResult<Void> result = authService.initiateLogin(loginRequest);

        return result.toResponseEntity();
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResult<Void>> logout(HttpServletRequest request,
                                                          HttpServletResponse response,
                                                          Authentication auth){
        ApiResult<Void> result = authService.logout(request, response, auth);
        return result.toResponseEntity();
    }
}
