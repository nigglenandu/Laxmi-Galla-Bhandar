package core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Refresh token entity – core security primitive.
 *
 * Stores hashed refresh tokens for revocation and rotation support.
 * Domain-agnostic: uses subject (string) instead of User entity reference.
 *
 * Features:
 *   - SHA-256 hashed token storage (plain token never persisted)
 *   - JTI (JWT ID) for advanced revocation/blacklisting
 *   - Auditing inherited from AuditableEntity (createdAt/updatedAt/createdBy/updatedBy)
 *   - Revocation flag + expiry
 *   - Optimistic locking (@Version from AuditableEntity)
 *   - Indexes for fast lookup (token_hash, subject, expiry_date)
 *   - Future-proof: device fingerprint column
 */
@Entity
@Table(
    name = "refresh_tokens",
    indexes = {
        @Index(name = "idx_refresh_token_hash", columnList = "token_hash", unique = true),
        @Index(name = "idx_refresh_token_subject", columnList = "subject"),
        @Index(name = "idx_refresh_token_expiry", columnList = "expiry_date"),
        @Index(name = "idx_refresh_token_revoked", columnList = "revoked")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"tokenHash"}) // Never log hash
public class RefreshToken extends AuditableEntity<Long> {

    /**
     * Subject identifier (usually email or user ID string)
     * - No @ManyToOne to keep core domain-agnostic
     * - App maps subject → user when needed
     */
    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    /**
     * SHA-256 hash of the plain refresh token
     * Plain token NEVER stored — only hash
     */
    @Column(name = "token_hash", nullable = false, unique = true, length = 88)
    private String tokenHash;

    /**
     * Optional JTI (JWT ID) claim — enables token-specific blacklisting
     */
    @Column(name = "jti", length = 36, unique = true)
    private String jti;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    @Builder.Default
    private boolean revoked = false;

    @Column(length = 45)
    private String ipAddress;

    @Column(name = "last_token_issued_at")
    private Instant lastTokenIssuedAt;

    @Column(length = 512)
    private String userAgent;

    @Column(length = 255)
    private String deviceFingerprint; // future: hash of device info

    protected void onCreate() {
        if (jti == null) {
            jti = UUID.randomUUID().toString();
        }
    }

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(Instant.now());
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }
}