package com.laxmi.galla.core.exception;

public class InvalidOperationException extends BusinessException {
    public InvalidOperationException(String reason) {
        super(reason, ErrorCode.BUSINESS_RULE_VIOLATION);
    }
}