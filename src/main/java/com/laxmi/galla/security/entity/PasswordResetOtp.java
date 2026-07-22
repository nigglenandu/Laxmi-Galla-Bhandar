package com.laxmi.galla.security.entity;

import com.laxmi.galla.domain.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "password_reset_otp")
public class PasswordResetOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User reference is required")
    @ManyToOne
    private User user;

    @NotNull(message = "Expiry date is required")
    private Instant expiryDate;

    @NotBlank(message = "OTP cannot be blank")
    @Size(max = 10, message = "OTP cannot exceed 10 characters")
    private String otp;

    private boolean used = false;
}
