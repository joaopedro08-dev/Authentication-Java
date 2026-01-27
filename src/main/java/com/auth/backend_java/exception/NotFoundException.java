package com.auth.backend_java.exception;

public class NotFoundException extends ApiException {

    public NotFoundException(String resource) {
        super(resource + " not found");
    }

    @Override
    public String getCode() {
        return "NOT_FOUND_404";
    }
}
