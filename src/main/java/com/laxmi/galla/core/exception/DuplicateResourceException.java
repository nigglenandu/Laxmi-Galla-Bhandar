package com.laxmi.galla.core.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends BusinessException {
    public DuplicateResourceException(String resource, String field, String value) {
        super(resource + " already exists with " + field + ": " + value,
                ErrorCode.DUPLICATE_RESOURCE,
                HttpStatus.CONFLICT);
    }
}