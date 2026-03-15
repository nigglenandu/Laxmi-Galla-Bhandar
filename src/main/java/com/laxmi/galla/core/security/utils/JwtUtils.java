package com.laxmi.galla.core.security.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Core / platform-level JWT utility – completely domain-agnostic.
 *
 * Responsibilities:
 *   - Low-level token building with custom claims
 *   - Strict token validation (signature + expiration + required claims)
 *   - Safe claim extraction
 *   - Secure cookie creation / clearing
 *
 * Does NOT know about any domain model (User, roles, etc.).
 * Domain-specific token generation belongs in higher-level services.
 *
 * Throws JwtValidationException on failure → enables centralized error handling.
 */
@Slf4j
@Component
public class JwtUtils {

    public static class JwtValidationException extends RuntimeException {
        public JwtValidationException(String message) {
            super(message);
        }

        public JwtValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // Short claim names → smaller JWT size (important for headers/cookies)
    private static final String CLAIM_USER_ID    = "uid";
    private static final String CLAIM_ROLES      = "roles";
    private static final String CLAIM_FIRST_NAME = "fn";
    private static final String CLAIM_LAST_NAME  = "ln";

    @Value("${platform.security.jwt.secret}")
    private String jwtSecretBase64;

    @Value("${platform.security.jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${platform.security.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Value("${platform.security.refresh-cookie.secure:true}")
    private boolean cookieSecure;

    @Value("${platform.security.refresh-cookie.same-site:Strict}")
    private String cookieSameSite;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        if (!StringUtils.hasText(jwtSecretBase64)) {
            throw new IllegalStateException("JWT secret is missing or empty");
        }

        signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretBase64.trim()));
        log.info("JWT signing key initialized successfully");
    }

    // ────────────────────────────────────────────────────────────────
    // Low-level token building (used by higher-level services)
    // ────────────────────────────────────────────────────────────────

    /**
     * Generic token builder – fully customizable claims.
     * Use this from domain-specific TokenService / AuthService.
     */
    public String buildToken(String subject, Map<String, Object> claims, long expirationMs) {
        if (!StringUtils.hasText(subject)) {
            throw new IllegalArgumentException("Subject cannot be empty");
        }

        JwtBuilder builder = Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(signingKey);

        if (claims != null) {
            claims.forEach(builder::claim);
        }

        return builder.compact();
    }

    // ────────────────────────────────────────────────────────────────
    // Validation – separate access vs refresh
    // ────────────────────────────────────────────────────────────────

    public void validateAccessToken(String token) throws JwtValidationException {
        validateTokenInternal(token, "access");
    }

    public void validateRefreshToken(String token) throws JwtValidationException {
        validateTokenInternal(token, "refresh");
    }

    private void validateTokenInternal(String token, String tokenType) throws JwtValidationException {
        if (!StringUtils.hasText(token)) {
            throw new JwtValidationException("Token is null or empty");
        }

        try {
            Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);

            Claims claims = parseClaims(token);

            if (claims.getSubject() == null) {
                throw new JwtValidationException("Missing subject claim");
            }

            // Access token requires roles
            if ("access".equals(tokenType) && claims.get(CLAIM_ROLES) == null) {
                throw new JwtValidationException("Missing roles claim in access token");
            }

            // Future extension point: revocation / JTI / blacklist / custom claims check

        } catch (ExpiredJwtException e) {
            throw new JwtValidationException("Token has expired", e);
        } catch (MalformedJwtException | SignatureException e) {
            throw new JwtValidationException("Invalid token signature or format", e);
        } catch (JwtException e) {
            throw new JwtValidationException("Token validation failed", e);
        } catch (Exception e) {
            log.error("Unexpected error validating {} token", tokenType, e);
            throw new JwtValidationException("Internal token validation error", e);
        }
    }

    // ────────────────────────────────────────────────────────────────
    // Claim Extraction (safe + null-friendly)
    // ────────────────────────────────────────────────────────────────

    public String getEmailFromToken(String token) {
        return safeGetClaim(token, Claims::getSubject);
    }

    public Long getUserIdFromToken(String token) {
        return safeGetClaim(token, claims -> claims.get(CLAIM_USER_ID, Long.class));
    }

    public List<String> getRolesFromToken(String token) {
        List<?> raw = safeGetClaim(token, claims -> claims.get(CLAIM_ROLES, List.class));
        if (raw == null) return Collections.emptyList();
        return raw.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.toUnmodifiableList());
    }

    public long getIssuedAtFromToken(String token) {
        Date issuedAt = safeGetClaim(token, Claims::getIssuedAt);
        return issuedAt != null ? issuedAt.getTime() / 1000L : 0L;
    }

    public Date getExpiration(String token) {
        return safeGetClaim(token, Claims::getExpiration);
    }

    private <T> T safeGetClaim(String token, Function<Claims, T> resolver) {
        if (!StringUtils.hasText(token)) return null;
        try {
            return resolver.apply(parseClaims(token));
        } catch (Exception e) {
            log.debug("Failed to extract claim from token: {}", e.getMessage());
            return null;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ────────────────────────────────────────────────────────────────
    // Secure Cookie Helpers
    // ────────────────────────────────────────────────────────────────

    public ResponseCookie createAccessCookie(String token) {
        return createCookie("access_token", token, accessTokenExpirationMs / 1000);
    }

    public ResponseCookie createRefreshCookie(String token) {
        return createCookie("refresh_token", token, refreshTokenExpirationMs / 1000);
    }

    public ResponseCookie clearAccessCookie() {
        return clearCookie("access_token");
    }

    public ResponseCookie clearRefreshCookie() {
        return clearCookie("refresh_token");
    }

    private ResponseCookie createCookie(String name, String value, long maxAgeSeconds) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .sameSite(cookieSameSite)
                .maxAge(maxAgeSeconds)
                .build();
    }

    private ResponseCookie clearCookie(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .sameSite(cookieSameSite)
                .maxAge(0)
                .build();
    }
}