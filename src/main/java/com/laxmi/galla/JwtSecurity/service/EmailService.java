package com.laxmi.galla.JwtSecurity.service;

import com.laxmi.galla.JwtSecurity.model.AuthUserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    public void sendVerification(AuthUserEntity user) {
        logger.info("Sending verification email to {} (dev stub)", user.getEmail());
    }
}