package com.example.common_module.errors;

import org.springframework.security.core.AuthenticationException;

public class CustomJwtException extends AuthenticationException {
    public CustomJwtException(String message) {
        super(message);
    }
}
