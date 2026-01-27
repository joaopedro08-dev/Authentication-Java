package com.auth.backend_java.service;

import com.auth.backend_java.repository.BlacklistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class BlacklistCleanupService {

    @Autowired
    private BlacklistRepository blacklistRepository;

    @Scheduled(cron = "0 */5 * * * ?")
    @Transactional
    public void cleanExpiredTokens() {
        blacklistRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}