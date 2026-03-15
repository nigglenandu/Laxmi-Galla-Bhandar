package com.laxmi.galla.security.service;

import com.laxmi.galla.core.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements IEmailService{
    private final JavaMailSender mailSender;

    @Override
    public void sendOtp(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Your OTP Code");
            message.setText("Your OTP is: " + otp + "\nIt will expire in 5 minutes.");

            mailSender.send(message);

            log.info("OTP email sent to {}", toEmail);

        } catch (Exception e) {

            log.error("Failed to send OTP email to {} | error={}", toEmail, e.getMessage());

            throw new BusinessException(
                    "Failed to send OTP email",
                    "EMAIL_SEND_FAILED"
            );
        }
    }
}
