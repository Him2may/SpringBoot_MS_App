package com.security_service.app.exceptions;

public class TokenRefreshException extends RuntimeException {
    public TokenRefreshException(String message) {super(message);
    }
}
