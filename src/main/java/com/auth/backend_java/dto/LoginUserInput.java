package com.auth.backend_java.dto;

public record LoginUserInput(
    String email,
    String password
) {
    
}
