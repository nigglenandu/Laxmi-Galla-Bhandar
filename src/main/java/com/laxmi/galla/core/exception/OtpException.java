package com.laxmi.galla.core.exception;

import com.laxmi.galla.core.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class OtpException extends BusinessException {

    public OtpException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.BAD_REQUEST);
    }

}