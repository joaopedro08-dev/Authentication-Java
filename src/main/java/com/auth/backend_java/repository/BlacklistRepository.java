package com.auth.backend_java.repository;

import com.auth.backend_java.model.BlacklistToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface BlacklistRepository extends JpaRepository<BlacklistToken, Long> {

    boolean existsByToken(String token);

    @Modifying
    void deleteByExpiresAtBefore(LocalDateTime now);
}