package com.auth.backend_java.dto;

public record RegisterUserInput(
    String name,
    String email,
    String password,
    String confirmPassword
) {
    
}
