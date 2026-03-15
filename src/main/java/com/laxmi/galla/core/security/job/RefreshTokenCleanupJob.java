package com.laxmi.galla.core.security.job;

import com.laxmi.galla.core.security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Scheduled cleanup of old refresh tokens.
 * Runs daily to keep the refresh_tokens table lean.
 * Uses configurable retention period from application properties.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupJob {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${platform.security.token-cleanup.retain-days:90}")
    private int cleanupRetainDays;

    /**
     * Daily cleanup at 4:00 AM server time.
     * Removes revoked tokens older than the configured retention period.
     * Also cleans any remaining expired tokens.
     */
    @Scheduled(cron = "0 0 4 * * ?") // 4:00 AM every day
    @Transactional
    public void cleanupOldTokens() {
        Instant threshold = Instant.now().minus(cleanupRetainDays, ChronoUnit.DAYS);

        int revokedDeleted = refreshTokenRepository.deleteRevokedExpiredBefore(threshold);
        int expiredDeleted = refreshTokenRepository.deleteExpiredBefore(threshold);

        log.info("Refresh token cleanup completed | retain-days={} | threshold={} | revoked_deleted={} | expired_deleted={}",
                cleanupRetainDays, threshold, revokedDeleted, expiredDeleted);
    }
}