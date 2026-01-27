package com.auth.backend_java.exception;

public class UnauthorizedException extends ApiException {

    public UnauthorizedException() {
        super("Unauthorized");
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    @Override
    public String getCode() {
        return "AUTH_401";
    }
}
