package com.laxmi.galla.security.controller;

import com.laxmi.galla.core.dto.response.ApiResult;
import com.laxmi.galla.security.dto.request.ChangePasswordRequest;
import com.laxmi.galla.security.dto.request.ForgotPasswordRequest;
import com.laxmi.galla.security.dto.request.ResetForgotPasswordRequest;
import com.laxmi.galla.security.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/password")
@Slf4j
@Tag(name = "Password Controller", description = "APIs for password management: forgot, reset, change")
public class PasswordController {
    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot")
    @Operation(summary = "Send forgot password OTP",
            description = "Sends an OTP to the user's email to reset password")
    public ResponseEntity<ApiResult<Void>> sendForgotPasswordOtp(@Valid @RequestBody ForgotPasswordRequest request){
        passwordResetService.sendForgotPasswordOtp(request);
        return ApiResult.<Void>ok(null, "OTP sent successfully")
                .toResponseEntity();
    }

    @PostMapping("/reset")
    @Operation(summary = "Reset password using OTP",
            description = "Resets the user's password after OTP verification")
    public ResponseEntity<ApiResult<Void>> resetPassword(@Valid @RequestBody ResetForgotPasswordRequest request){
        passwordResetService.resetPassword(request);

        return ApiResult.<Void>ok(null, "Password reset successful")
                .toResponseEntity();
    }

    @PostMapping("/change")
    @Operation(summary = "Change password",
            description = "Allows authenticated user to change their password")
    public ResponseEntity<ApiResult<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication auth){
        passwordResetService.changePassword(auth, request);
        return ApiResult.<Void>ok(null, "Password changed successfully")
                .toResponseEntity();
    }
}
