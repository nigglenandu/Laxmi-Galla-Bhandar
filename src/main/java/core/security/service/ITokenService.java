package core.security.service;

import core.dto.response.TokenPair;
import core.exception.TokenReuseDetectedException;
import core.exception.TokenValidationException;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * Public API for secure token generation, rotation, revocation and cookie management.
 * <p>
 * Implementations are responsible for:
 * <ul>
 *   <li>JWT creation & signing</li>
 *   <li>Secure persistence of hashed refresh tokens</li>
 *   <li>Revocation and rotation logic</li>
 * </ul>
 */
public interface ITokenService {

    /**
     * Generates a new access + refresh token pair and persists the hashed refresh token.
     *
     * @param subject           typically user ID or email (non-null, non-empty)
     * @param claims            additional JWT claims (roles, permissions, etc.)
     * @param ipAddress         client IP (for audit/logging, nullable)
     * @param userAgent         client user-agent (nullable)
     * @param deviceFingerprint optional device identifier/fingerprint (nullable)
     * @return plain access + refresh token pair (never persist these)
     * @throws IllegalArgumentException if subject is empty or claims are invalid
     */
    TokenPair generateTokens(
            String subject,
            Map<String, Object> claims,
            @Nullable String ipAddress,
            @Nullable String userAgent,
            @Nullable String deviceFingerprint);

    /**
     * Validates the provided refresh token, revokes the old one if valid,
     * and issues a new access + refresh token pair.
     *
     * @param oldRefreshTokenPlain the current refresh token value
     * @param freshClaims          updated claims (must be re-fetched from DB)
     * @return new token pair
     * @throws TokenValidationException     if token is invalid, expired or not found
     * @throws TokenReuseDetectedException  if token was already revoked (security incident)
     * @throws IllegalArgumentException     if token or claims are empty/invalid
     */
    TokenPair rotateRefreshToken(String oldRefreshTokenPlain, Map<String, Object> freshClaims);

    /**
     * Revokes a single refresh token by its plain-text value (if it exists).
     * No-op if token is empty or not found.
     */
    void revokeRefreshToken(String refreshTokenPlain);

    /**
     * Revokes **all** active refresh tokens for the given subject
     * (effectively logs out the user from all devices/sessions).
     */
    void revokeAllForSubject(String subject);

    /**
     * Revokes refresh tokens associated with a specific device fingerprint.
     *
     * @return number of tokens that were revoked (0 if none found or invalid input)
     */
    int revokeByDeviceFingerprint(String subject, @Nullable String deviceFingerprint);

    /**
     * Attaches HttpOnly, Secure cookies containing the access and refresh tokens
     * to the HTTP response.
     */
    void addTokensToResponse(HttpServletResponse response, String accessToken, String refreshToken);

    /**
     * Invalidates (clears) both access and refresh token cookies in the response.
     */
    void clearTokens(HttpServletResponse response);
}