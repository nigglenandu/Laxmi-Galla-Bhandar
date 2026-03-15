package com.laxmi.galla.security.service;

import com.laxmi.galla.security.dto.request.ChangePasswordRequest;
import com.laxmi.galla.security.dto.request.ForgotPasswordRequest;
import com.laxmi.galla.security.dto.request.ResetForgotPasswordRequest;
import org.springframework.security.core.Authentication;

public interface IPasswordResetService {
    void sendForgotPasswordOtp(ForgotPasswordRequest request);

    void resetPassword(ResetForgotPasswordRequest request);

    void changePassword(Authentication auth, ChangePasswordRequest request);
}
