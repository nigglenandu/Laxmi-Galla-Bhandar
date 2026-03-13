//package com.laxmi.galla.entity;
//
//import jakarta.persistence.*;
//import java.time.Instant;
//import java.util.UUID;
//
//@Entity
//@Table(name = "refresh_tokens", indexes = {
//        @Index(name = "idx_refresh_token_token", columnList = "token", unique = true)
//})
//public class RefreshToken {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, unique = true, length = 200)
//    private String token;
//
//    @Column(nullable = false, unique = true)
//    private String tokenHash;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private AuthUserEntity user;
//
//    @Column(nullable = false)
//    private Instant expiryDate;
//
//    @Column(nullable = false)
//    private boolean revoked = false;
//
//    private String ipAddress;
//    private String userAgent;
//
//    @PrePersist
//    public void prePersist() {
//        if (token == null) token = UUID.randomUUID().toString();
//    }
//
//    // Getters and setters
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//
//    public String getToken() { return token; }
//    public void setToken(String token) { this.token = token; }
//
//    public String getTokenHash() { return tokenHash; }
//    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }
//
//    public AuthUserEntity getUser() { return user; }
//    public void setUser(AuthUserEntity user) { this.user = user; }
//
//    public Instant getExpiryDate() { return expiryDate; }
//    public void setExpiryDate(Instant expiryDate) { this.expiryDate = expiryDate; }
//
//    public boolean isRevoked() { return revoked; }
//    public void setRevoked(boolean revoked) { this.revoked = revoked; }
//
//    public String getIpAddress() { return ipAddress; }
//    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
//
//    public String getUserAgent() { return userAgent; }
//    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
//
//    // Builder pattern
//    public static class Builder {
//        private Long id;
//        private String token;
//        private String tokenHash;
//        private AuthUserEntity user;
//        private Instant expiryDate;
//        private boolean revoked;
//        private String ipAddress;
//        private String userAgent;
//
//        public Builder id(Long id) { this.id = id; return this; }
//        public Builder token(String token) { this.token = token; return this; }
//        public Builder tokenHash(String tokenHash) { this.tokenHash = tokenHash; return this; }
//        public Builder user(AuthUserEntity user) { this.user = user; return this; }
//        public Builder expiryDate(Instant expiryDate) { this.expiryDate = expiryDate; return this; }
//        public Builder revoked(boolean revoked) { this.revoked = revoked; return this; }
//        public Builder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
//        public Builder userAgent(String userAgent) { this.userAgent = userAgent; return this; }
//
//        public RefreshToken build() {
//            RefreshToken token = new RefreshToken();
//            token.setId(this.id);
//            token.setToken(this.token);
//            token.setTokenHash(this.tokenHash);
//            token.setUser(this.user);
//            token.setExpiryDate(this.expiryDate);
//            token.setRevoked(this.revoked);
//            token.setIpAddress(this.ipAddress);
//            token.setUserAgent(this.userAgent);
//            return token;
//        }
//    }
//
//    public static Builder builder() {
//        return new Builder();
//    }
//}
