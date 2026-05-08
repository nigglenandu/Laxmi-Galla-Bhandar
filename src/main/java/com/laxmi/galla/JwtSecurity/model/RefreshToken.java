package com.laxmi.galla.JwtSecurity.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
