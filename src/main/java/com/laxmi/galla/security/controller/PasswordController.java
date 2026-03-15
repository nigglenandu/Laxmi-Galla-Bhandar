package com.laxmi.galla.security.controller;

import com.laxmi.galla.core.dto.response.ApiResult;
import com.laxmi.galla.security.dto.request.ChangePasswordRequest;
import com.laxmi.galla.security.dto.request.ForgotPasswordRequest;
import com.laxmi.galla.security.dto.request.ResetForgotPasswordRequest;
import com.laxmi.galla.security.service.PasswordResetService;
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
public class PasswordController {
    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot")
    public ResponseEntity<ApiResult<Void>> sendForgotPasswordOtp(@Valid @RequestBody ForgotPasswordRequest request){
        passwordResetService.sendForgotPasswordOtp(request);
        return ApiResult.<Void>ok(null, "OTP sent successfully")
                .toResponseEntity();
    }

    @PostMapping("/reset")
    public ResponseEntity<ApiResult<Void>> resetPassword(@Valid @RequestBody ResetForgotPasswordRequest request){
        passwordResetService.resetPassword(request);

        return ApiResult.<Void>ok(null, "Password reset successful")
                .toResponseEntity();
    }

    @PostMapping("/change")
    public ResponseEntity<ApiResult<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication auth){
        passwordResetService.changePassword(auth, request);
        return ApiResult.<Void>ok(null, "Password changed successfully")
                .toResponseEntity();
    }
}
