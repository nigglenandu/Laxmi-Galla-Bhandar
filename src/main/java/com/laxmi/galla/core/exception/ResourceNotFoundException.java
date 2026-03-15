package com.laxmi.galla.core.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resource, String identifier) {
        super(resource + " not found with id: " + identifier,
              ErrorCode.RESOURCE_NOT_FOUND,
              HttpStatus.NOT_FOUND);
    }
}
