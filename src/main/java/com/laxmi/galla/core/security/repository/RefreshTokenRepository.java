package com.laxmi.galla.core.security.repository;

import com.laxmi.galla.core.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for core refresh tokens.
 *
 * Provides secure, efficient access to refresh token persistence.
 * All operations are read/write on hashed tokens only — never plain tokens.
 *
 * Designed for:
 *   - Fast lookup by hash (login/refresh)
 *   - Bulk revocation by subject (logout all devices)
 *   - Cleanup expired tokens (cron job)
 *   - Future extensions: device-based revocation, stats, multi-tenant filters
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Primary lookup: find by SHA-256 hash of plain refresh token
     */
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /**
     * Lookup by JTI (for advanced blacklisting or revocation by token ID)
     */
    Optional<RefreshToken> findByJti(String jti);

    /**
     * Check if any active (non-revoked) token exists for subject
     * Useful for "is user currently logged in" checks
     */
    @Transactional(readOnly = true)
    boolean existsBySubjectAndRevokedFalse(String subject);

    /**
     * Count active tokens for a subject (e.g. multi-device count)
     */
    @Transactional(readOnly = true)
    long countBySubjectAndRevokedFalse(String subject);

    /**
     * Find all tokens for a subject (e.g. for multi-device logout UI)
     */
    @Transactional(readOnly = true)
    List<RefreshToken> findBySubject(String subject);

    /**
     * Revoke all tokens for a subject (logout from all devices)
     */
    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.subject = :subject")
    int revokeAllBySubject(@Param("subject") String subject);

    /**
     * Delete all revoked tokens older than given date
     * (cleanup job to prevent DB bloat)
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken rt WHERE rt.revoked = true AND rt.expiryDate < :before")
    int deleteRevokedExpiredBefore(@Param("before") Instant before);

    /**
     * Delete all expired tokens (revoked or not) — cleanup job
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :before")
    int deleteExpiredBefore(@Param("before") Instant before);

    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.subject = :subject AND rt.deviceFingerprint = :fingerprint")
    int revokeBySubjectAndDeviceFingerprint(@Param("subject") String subject, @Param("fingerprint") String fingerprint);
}