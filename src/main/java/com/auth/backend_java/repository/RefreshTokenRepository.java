package com.auth.backend_java.repository;

import com.auth.backend_java.model.RefreshToken;
import com.auth.backend_java.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    
    @Modifying
    void deleteByUser(UserModel user);

    @Modifying
    void deleteByExpiryDateBefore(Instant now);
}