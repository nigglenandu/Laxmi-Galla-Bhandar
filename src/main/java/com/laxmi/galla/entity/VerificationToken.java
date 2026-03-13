//package com.laxmi.galla.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "verification_token")
//public class VerificationToken {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, unique = true)
//    private String token;
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false, unique = true)
//    private AuthUserEntity user;
//
//    @Column(nullable = false)
//    private LocalDateTime expiryDate;
//
//    public VerificationToken(String token, AuthUserEntity user, LocalDateTime expiryDate) {
//        this.token = token;
//        this.user = user;
//        this.expiryDate = expiryDate;
//    }
//
//    public VerificationToken() {
//
//    }
//
//    public LocalDateTime getExpiryDate() {
//        return expiryDate;
//    }
//
//    public void setExpiryDate(LocalDateTime expiryDate) {
//        this.expiryDate = expiryDate;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getToken() {
//        return token;
//    }
//
//    public void setToken(String token) {
//        this.token = token;
//    }
//
//    public AuthUserEntity getUser() {
//        return user;
//    }
//
//    public void setUser(AuthUserEntity user) {
//        this.user = user;
//    }
//}
