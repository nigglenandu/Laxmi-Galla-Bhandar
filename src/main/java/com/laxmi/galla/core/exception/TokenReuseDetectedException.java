package com.laxmi.galla.core.exception;

import org.springframework.http.HttpStatus;

public class TokenReuseDetectedException extends TokenValidationException {

    private final String subject;
    private final String jti;

    public TokenReuseDetectedException(String message, String subject, String jti) {
        super(message + " | subject=" + subject + " | jti=" + jti,
                ErrorCode.TOKEN_REUSED, HttpStatus.UNAUTHORIZED);
        this.subject = subject;
        this.jti = jti;
    }

    public String getSubject() {
        return subject;
    }

    public String getJti() {
        return jti;
    }
}