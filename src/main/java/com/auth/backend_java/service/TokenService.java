package com.auth.backend_java.service;

import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import com.auth.backend_java.model.UserModel;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Optional;

@Component
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    private static final String ISSUER = "api-auth"; 

    public String generateToken(UserModel user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(user.getEmail())
                    .withClaim("id", user.getId())
                    .withClaim("role", user.getRole().name())
                    .withExpiresAt(getExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token", exception);
        }
    }

    public Optional<DecodedJWT> getDecodedToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return Optional.of(JWT.require(algorithm)
                    .withIssuer(ISSUER) 
                    .build()
                    .verify(token));
        } catch (JWTVerificationException exception) {
            return Optional.empty();
        }
    }

    public String generateRefreshToken(UserModel user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().plus(Duration.ofDays(7)))
                .sign(algorithm);
    }

    private Instant getExpirationDate() {
        return LocalDateTime.now().plusMinutes(15).atZone(ZoneId.of("America/Sao_Paulo")).toInstant();
    }
}