package core.security.service;

import core.dto.response.TokenPair;
import core.exception.TokenReuseDetectedException;
import core.exception.TokenValidationException;
import core.model.RefreshToken;
import core.security.repository.RefreshTokenRepository;
import core.security.utils.HashUtils;
import core.security.utils.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

/**
 * Core token service – stateless (except for refresh token persistence), domain-agnostic.
 *
 * Responsibilities:
 *   - Generate access + refresh token pairs + persist hashed refresh token
 *   - Rotate refresh tokens (validate → revoke old → issue new pair with fresh claims)
 *   - Revoke single token, all tokens, or per-device tokens (using hashed deviceFingerprint)
 *   - Add / clear secure cookies
 *   - Validation & revocation checks
 *   - Reuse detection with security alerts
 *
 * Design invariants:
 *   - NEVER stores plain refresh tokens or raw device fingerprints — only SHA-256 hashes
 *   - Preserves audit trail (revoked tokens stay in DB with timestamps)
 *   - Ready for cleanup jobs, multi-device logout, device fingerprinting, anomaly detection
 *
 * Applications MUST:
 *   - Call from AuthService / LoginController
 *   - Provide subject (e.g. email) and claims map
 *   - Pass IP/user-agent/deviceFingerprint if tracking is needed
 *   - Supply fresh claims on rotation (roles/permissions must be re-fetched)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements ITokenService{

    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${platform.security.jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${platform.security.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Value("${platform.security.refresh-token.byte-length:48}")
    private int refreshTokenByteLength;

    /**
     * Generate new access + refresh token pair for a subject.
     * Persists hashed refresh token in DB.
     * Returns plain tokens — caller handles cookies/response.
     */
    @Override
    @Transactional
    public TokenPair generateTokens(String subject, Map<String, Object> claims,
                                    String ipAddress, String userAgent, String deviceFingerprint) {
        if (!StringUtils.hasText(subject)) {
            throw new IllegalArgumentException("Subject cannot be empty");
        }

        String accessToken = jwtUtils.buildToken(subject, claims, accessTokenExpirationMs);
        String refreshTokenPlain = generateSecureRefreshToken();

        RefreshToken entity = RefreshToken.builder()
                .subject(subject)
                .tokenHash(HashUtils.sha256Base64(refreshTokenPlain))
                .jti(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpirationMs))
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .deviceFingerprint(HashUtils.sha256Base64(deviceFingerprint))
                .revoked(false)
                .build();

        refreshTokenRepository.save(entity);

        log.debug("Generated & persisted refresh token | subject={} | jti={} | device={}",
                subject, entity.getJti(), deviceFingerprint != null ? "present" : "absent");

        return new TokenPair(accessToken, refreshTokenPlain);
    }

    /**
     * Rotate refresh token: validate old → revoke old → issue new pair.
     * Throws on failure (expired, revoked, reused).
     */
    @Override
    @Transactional
    public TokenPair rotateRefreshToken(String oldRefreshTokenPlain, Map<String, Object> freshClaims) {
        if (!StringUtils.hasText(oldRefreshTokenPlain)) {
            throw new IllegalArgumentException("Refresh token cannot be empty");
        }

        if (freshClaims == null || freshClaims.isEmpty()) {
            throw new IllegalArgumentException("Fresh claims must be provided on refresh (roles/permissions)");
        }

        String hash = HashUtils.sha256Base64(oldRefreshTokenPlain);
        RefreshToken oldToken = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new TokenValidationException("Refresh token not found"));

        if (oldToken.isRevoked()) {
            revokeAllForSubject(oldToken.getSubject());
            log.warn("SECURITY ALERT: Token reuse detected | subject={} | oldJti={} | ip={} | userAgent={}",
                    oldToken.getSubject(), oldToken.getJti(), oldToken.getIpAddress(), oldToken.getUserAgent());
            throw new TokenReuseDetectedException("Token reuse detected", oldToken.getSubject(), oldToken.getJti());
        }
        if (oldToken.isExpired()) {
            revokeRefreshToken(oldToken);
            throw new TokenValidationException("Refresh token expired");
        }

        revokeRefreshToken(oldToken);

        TokenPair newPair = generateTokens(
                oldToken.getSubject(),
                freshClaims,
                oldToken.getIpAddress(),
                oldToken.getUserAgent(),
                oldToken.getDeviceFingerprint()
        );

        log.info("Refresh token rotated | subject={} | oldJti={} → newJti={}",
                oldToken.getSubject(), oldToken.getJti(), safeToken(newPair.refreshToken()));

        return newPair;
    }

    /**
     * Revoke a single refresh token by plain value.
     */
    @Override
    @Transactional
    public void revokeRefreshToken(String refreshTokenPlain) {
        if (!StringUtils.hasText(refreshTokenPlain)) {
            return;
        }

        String hash = HashUtils.sha256Base64(refreshTokenPlain);
        refreshTokenRepository.findByTokenHash(hash)
                .ifPresent(this::revokeRefreshToken);
    }

    private void revokeRefreshToken(RefreshToken token) {
        token.setRevoked(true);
        refreshTokenRepository.save(token);
        log.info("Refresh token revoked | subject={} | jti={}", token.getSubject(), token.getJti());
    }

    /**
     * Revoke ALL refresh tokens for a subject (logout from all devices).
     * Marks tokens as revoked — preserves audit trail.
     */
    @Override
    @Transactional
    public void revokeAllForSubject(String subject) {
        if (!StringUtils.hasText(subject)) {
            return;
        }

        int revokedCount = refreshTokenRepository.revokeAllBySubject(subject);
        log.warn("Revoked {} active refresh tokens for subject: {}", revokedCount, subject);
    }

    /**
     * Revoke refresh tokens for a specific device (using deviceFingerprint).
     */
    @Override
    @Transactional
    public int revokeByDeviceFingerprint(String subject, String deviceFingerprint) {
        if (!StringUtils.hasText(subject) || !StringUtils.hasText(deviceFingerprint)) {
            return 0;
        }

        String hashedFingerprint = HashUtils.sha256Base64(deviceFingerprint);
        if (hashedFingerprint == null) {
            return 0;
        }

        int revokedCount = refreshTokenRepository.revokeBySubjectAndDeviceFingerprint(subject, hashedFingerprint);
        if (revokedCount > 0) {
            log.warn("Revoked {} tokens for suspicious device | subject={} | fingerprint_hash={}",
                    revokedCount, subject, safeToken(hashedFingerprint));
        }
        return revokedCount;
    }

    /**
     * Add access & refresh cookies to response (HttpOnly, Secure, SameSite).
     */
    @Override
    public void addTokensToResponse(HttpServletResponse response,
                                    String accessToken,
                                    String refreshToken) {
        response.addHeader(HttpHeaders.SET_COOKIE, jwtUtils.createAccessCookie(accessToken).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, jwtUtils.createRefreshCookie(refreshToken).toString());
    }

    /**
     * Clear both access & refresh cookies (logout).
     */
    @Override
    public void clearTokens(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, jwtUtils.clearAccessCookie().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, jwtUtils.clearRefreshCookie().toString());
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private String generateSecureRefreshToken() {
        byte[] bytes = new byte[refreshTokenByteLength];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String safeToken(String token) {
        return token != null && token.length() > 8 ? token.substring(0, 8) + "..." : "[invalid]";
    }
}