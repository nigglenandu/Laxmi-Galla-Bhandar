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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService implements IPasswordResetService {
    private final UserRepository userRepository;
    private final OtpService otpService;
    private final IEmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public void sendForgotPasswordOtp(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));

        String otp = otpService.generateOtp(user.getEmail(), OtpPurpose.PASSWORD_RESET);
        emailService.sendOtp(user.getEmail(), otp);
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
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        log.info("Password reset successfully for email {}", user.getEmail());
    }

    public void changePassword(Authentication authh, ChangePasswordRequest request) {
//        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
//            // Log warning and handle gracefully
//            log.warn("Unauthenticated request for password change.");
//            throw new BusinessException(
//                    "Authentication required",
//                    "AUTH_UNAUTHORIZED",
//                    org.springframework.http.HttpStatus.UNAUTHORIZED
//            );
//        }
//
//        String email = auth.getName();
//        log.info("Changing password for user: {}", email);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            log.warn("Unauthenticated request for password change.");
            throw new BusinessException(
                    "Authentication required",
                    "AUTH_UNAUTHORIZED",
                    HttpStatus.UNAUTHORIZED
            );
        }

        String email = auth.getName();
        log.info("Changing password for user: {}", email);



        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BusinessException("INVALID_CURRENT_PASSWORD", "Current password is incorrect");
        }

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BusinessException("PASSWORD_MISMATCH", "New password and confirm password do not match");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BusinessException("PASSWORD_SAME", "New password cannot be the same as current password");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        log.info("Password changed successfully | email={}", email);
    }

}
