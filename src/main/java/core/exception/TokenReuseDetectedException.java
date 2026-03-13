package core.exception;

import org.springframework.http.HttpStatus;

public class TokenReuseDetectedException extends TokenValidationException {
    public TokenReuseDetectedException(String message, String subject, String jti) {
        super(message + " | subject=" + subject + " | jti=" + jti,
              ErrorCode.TOKEN_REUSED, HttpStatus.UNAUTHORIZED);
    }
}