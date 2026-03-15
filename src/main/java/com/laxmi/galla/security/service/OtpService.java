package com.laxmi.galla.security.service;

import com.laxmi.galla.core.exception.OtpException;
import com.laxmi.galla.security.enums.OtpPurpose;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final StringRedisTemplate redisTemplate;

    private static final long OTP_EXPIRATION_MINUTES = 5;

    public String generateOtp(String email, OtpPurpose purpose) {

        String otp = generateOtp();
        String key = buildKey(email, purpose);

        redisTemplate.opsForValue().set(
                key,
                otp,
                Duration.ofMinutes(OTP_EXPIRATION_MINUTES)
        );

        log.info("OTP generated | purpose={} | email={}", purpose, email);

        return otp;
    }

    public void verifyOtp(String email, String otp, OtpPurpose purpose) {

        String key = buildKey(email, purpose);

        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp == null) {
            throw new OtpException("OTP expired or not found", "OPT_EXPIRED");
        }

        if (!storedOtp.equals(otp)) {
            throw new OtpException("Invalid OTP", "OTP_INVALID");
        }

        redisTemplate.delete(key);

        log.info("OTP verified | purpose={} | email={}", purpose, email);
    }

    private String buildKey(String email, OtpPurpose purpose) {
        return "otp:" + purpose + ":" + email;
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1_000_000));
    }
}