package com.laxmi.galla.security.entity;

import com.laxmi.galla.entity.AuthUserEntity;
import core.security.utils.HashUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens",
        indexes = @Index(columnList = "token_hash", unique = true))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tokenHash;           // SHA-256 or better of the real token

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AuthUserEntity user;

    @Column(nullable = false)
    private Instant expiryDate;

    private boolean revoked = false;

    private String ipAddress;           // optional but useful for audit

    private String userAgent;           // optional

    private String deviceFingerprintHash; // optional – sha256 of device info

    @Column(updatable = false)
    private Instant createdAt = Instant.now();

    // Helper method (used during generation)
    public static RefreshToken createForUser(
            AuthUserEntity user,
            String plainRefreshToken,
            Instant expiry,
            String ip,
            String ua,
            String deviceFp) {

        return RefreshToken.builder()
                .user(user)
                .tokenHash(HashUtils.sha256Base64(plainRefreshToken))  // or BCrypt / Argon2
                .expiryDate(expiry)
                .ipAddress(ip)
                .userAgent(ua)
                .deviceFingerprintHash(deviceFp != null ? HashUtils.sha256Base64(deviceFp) : null)
                .build();
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }
}