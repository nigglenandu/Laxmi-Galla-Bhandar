package com.laxmi.galla.JwtSecurity.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.StringJoiner;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String tokenHash;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AuthUserEntity user;

    @Column(name = "expiry_date")
    private Instant expiryDate;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "id_address")
    private String ipAddress;

    public RefreshToken() {
    }

    public RefreshToken(Long id, String tokenHash, AuthUserEntity user, Instant expiryDate, String userAgent, String ipAddress) {
        this.id = id;
        this.tokenHash = tokenHash;
        this.user = user;
        this.expiryDate = expiryDate;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
    }

    public Long getId() {
        return id;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public AuthUserEntity getUser() {
        return user;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public void setUser(AuthUserEntity user) {
        this.user = user;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RefreshToken.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("tokenHash='" + tokenHash + "'")
                .add("user=" + user)
                .add("expiryDate=" + expiryDate)
                .add("userAgent='" + userAgent + "'")
                .add("ipAddress='" + ipAddress + "'")
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String tokenHash;
        private AuthUserEntity user;
        private Instant expiryDate;
        private String userAgent;
        private String ipAddress;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder tokenHash(String tokenHash) {
            this.tokenHash = tokenHash;
            return this;
        }

        public Builder user(AuthUserEntity user) {
            this.user = user;
            return this;
        }

        public Builder expiryDate(Instant expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public RefreshToken build() {
            return new RefreshToken(id, tokenHash, user, expiryDate, userAgent, ipAddress);
        }
    }
}