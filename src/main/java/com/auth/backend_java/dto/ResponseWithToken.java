package com.auth.backend_java.dto;

import java.util.Optional;

public record ResponseWithToken(
    String message,
    boolean success,
    Optional<String> token
) {}