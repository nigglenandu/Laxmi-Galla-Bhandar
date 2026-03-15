package com.laxmi.galla.security.service;

import com.laxmi.galla.core.exception.BusinessException;
import com.laxmi.galla.core.security.utils.HashUtils;
import com.laxmi.galla.entity.User;
import com.laxmi.galla.repository.UserRepository;
import com.laxmi.galla.security.dto.request.ChangePasswordRequest;
import com.laxmi.galla.security.dto.request.ForgotPasswordRequest;
import com.laxmi.galla.security.dto.request.ResetForgotPasswordRequest;
import com.laxmi.galla.security.enums.OtpPurpose;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {
    private final UserRepository userRepository;
    private final OtpService otpService;
    private final IEmailService emailService;

    public void sendForgotPasswordOtp(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));

        String otp = otpService.generateOtp(user.getEmail(), OtpPurpose.PASSWORD_RESET);

        // TODO: Integrate your email service here
        log.info("Sending OTP '{}' to email {}", otp, user.getEmail());
    }

    /**
     * Step 2: Verify OTP and reset password
     */
    public void resetPassword(ResetForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));

        // Validate OTP
        otpService.verifyOtp(user.getEmail(), request.otp(), OtpPurpose.PASSWORD_RESET);

        // Validate password match
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BusinessException("PASSWORD_MISMATCH", "New password and confirm password do not match");
        }

        // Encode and update password
        user.setPassword(HashUtils.sha256Base64(request.newPassword()));
        userRepository.save(user);

        log.info("Password reset successfully for email {}", user.getEmail());
    }

    public void changePassword(Authentication auth, ChangePasswordRequest request) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));

        // Verify current password
        String currentPasswordHash = HashUtils.sha256Base64(request.currentPassword());

        if (!currentPasswordHash.equals(user.getPassword())) {
            throw new BusinessException("INVALID_CURRENT_PASSWORD", "Current password is incorrect");
        }

        // Validate new password confirmation
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BusinessException("PASSWORD_MISMATCH", "New password and confirm password do not match");
        }

        // Prevent same password reuse
        String newPasswordHash = HashUtils.sha256Base64(request.newPassword());

        if (newPasswordHash.equals(user.getPassword())) {
            throw new BusinessException("PASSWORD_SAME", "New password cannot be the same as current password");
        }

        // Update password
        user.setPassword(newPasswordHash);
        userRepository.save(user);

        log.info("Password changed successfully | email={}", email);
    }

}
