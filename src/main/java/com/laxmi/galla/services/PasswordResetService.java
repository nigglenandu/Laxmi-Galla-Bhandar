//package com.laxmi.galla.services;
//
//import com.laxmi.galla.JwtSecurity.EmailVerification.EmailService;
//import com.laxmi.galla.JwtSecurity.repository.AuthUserRepository;
//import com.laxmi.galla.JwtSecurity.repository.PasswordResetOtpRepo;
//import com.laxmi.galla.entity.AuthUserEntity;
//import com.laxmi.galla.entity.PasswordResetOtp;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import java.time.LocalDateTime;
//
//@Service
//public class PasswordResetService {
//
//    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
//
//    private final PasswordResetOtpRepo otpRepo;
//    private final AuthUserRepository userRepo;
//    private final EmailService emailService;
//    private final PasswordEncoder encoder;
//
//    public PasswordResetService(EmailService emailService, PasswordResetOtpRepo otpRepo,
//                                AuthUserRepository userRepo, PasswordEncoder encoder) {
//        this.emailService = emailService;
//        this.otpRepo = otpRepo;
//        this.userRepo = userRepo;
//        this.encoder = encoder;
//    }
//
//    // ========================
//    // SEND OTP
//    // ========================
//    public void sendOtp(String email){
//        log.info("Generating OTP for email={}", email);
//
//        AuthUserEntity user = userRepo.findByEmailIgnoreCase(email)
//                .orElseThrow(() -> {
//                    log.error("Email not found: {}", email);
//                    return new RuntimeException("Email not found");
//                });
//
//        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
//        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);
//
//        log.debug("Generated OTP={} for email={} expires at={}", otp, email, expiry);
//
//        PasswordResetOtp passwordOtp = new PasswordResetOtp();
//        passwordOtp.setUser(user);
//        passwordOtp.setOtp(otp);
//        passwordOtp.setExpiryDate(expiry);
//
//        otpRepo.save(passwordOtp);
//        log.info("OTP saved in DB for email={}", email);
//
//        emailService.sendEmail(user.getEmail(), "Password Reset OTP", "Your OTP is: " + otp);
//        log.info("OTP email sent to {}", email);
//    }
//
//    // ========================
//    // RESET FORGOT PASSWORD
//    // ========================
//    public void resetForgotPassword(String email, String otp, String newPassword, String confirmPassword){
//
//        log.info("Resetting password for email={} using OTP={}", email, otp);
//
//        if (!newPassword.equals(confirmPassword)) {
//            log.warn("Password mismatch for email={}", email);
//            throw new RuntimeException("Password & Confirm Password do not match");
//        }
//
//        AuthUserEntity user = userRepo.findByEmailIgnoreCase(email)
//                .orElseThrow(() -> {
//                    log.error("Email not found: {}", email);
//                    return new RuntimeException("Email not found");
//                });
//
//        PasswordResetOtp passwordOtp = otpRepo.findByUserAndOtpAndUsedFalse(user, otp)
//                .orElseThrow(() -> {
//                    log.error("Invalid OTP={} for email={}", otp, email);
//                    return new RuntimeException("Invalid OTP");
//                });
//
//        if(passwordOtp.getExpiryDate().isBefore(LocalDateTime.now())){
//            log.warn("Expired OTP={} for email={}", otp, email);
//            throw new RuntimeException("OTP expired");
//        }
//
//        user.setPassword(encoder.encode(newPassword));
//        userRepo.save(user);
//        log.info("Password updated for email={}", email);
//
//        passwordOtp.setUsed(true);
//        otpRepo.save(passwordOtp);
//        log.info("OTP marked as used for email={}", email);
//    }
//
//    // ========================
//    // CHANGE PASSWORD
//    // ========================
//    public void changePassword(String oldPassword, String newPassword, String confirmPassword) {
//
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        log.info("User {} requested password change", username);
//
//        AuthUserEntity user = userRepo.findByUsername(username)
//                .orElseThrow(() -> {
//                    log.error("User not found: {}", username);
//                    return new RuntimeException("User not found");
//                });
//
//        if (!encoder.matches(oldPassword, user.getPassword())) {
//            log.warn("Invalid old password for username={}", username);
//            throw new RuntimeException("Old password is incorrect");
//        }
//
//        if (!newPassword.equals(confirmPassword)) {
//            log.warn("Password mismatch for username={}", username);
//            throw new RuntimeException("Password & Confirm Password do not match");
//        }
//
//        user.setPassword(encoder.encode(newPassword));
//        userRepo.save(user);
//
//        log.info("Password changed successfully for username={}", username);
//    }
//}
