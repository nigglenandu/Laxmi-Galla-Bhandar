package com.laxmi.galla.security.service;

public interface IEmailService {
    void sendOtp(String toEmail, String otp);
}
