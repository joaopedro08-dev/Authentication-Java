package com.auth.backend_java.exception;

public class ForbiddenException extends ApiException {

    public ForbiddenException() {
        super("Forbidden");
    }

    public ForbiddenException(String message) {
        super(message);
    }

    @Override
    public String getCode() {
        return "AUTH_403";
    }
}
