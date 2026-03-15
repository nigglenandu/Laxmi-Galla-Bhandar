package com.laxmi.galla.core.exception;

import org.springframework.http.HttpStatus;

public class InsufficientPermissionException extends BusinessException {
    public InsufficientPermissionException(String action) {
        super("Insufficient permission to perform: " + action,
                ErrorCode.AUTH_FORBIDDEN,
                HttpStatus.FORBIDDEN);
    }
}