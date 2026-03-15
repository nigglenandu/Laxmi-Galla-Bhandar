package com.laxmi.galla.entity;

import com.laxmi.galla.security.entity.RoleEntity;
import com.laxmi.galla.core.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(exclude = {"password"})   // ← important!
public class User extends AuditableEntity<Long> {

    @Column(nullable = false, unique = true)
    String firstName;

    @Column(nullable = false, unique = true)
    String lastName;

    @Column(nullable = false)
    String password;                    // must be hashed!

    @Column(nullable = false, unique = true)
    String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    Set<RoleEntity> roles = new HashSet<>();

    @Builder.Default
    boolean emailVerified = false;

    @Builder.Default
    boolean active = true;

    // Very useful in practice
    Instant lastLoginAt;
    String lastLoginIp;

    // Optional: if you want to track failed login attempts
    @Builder.Default
    int failedLoginAttempts = 0;
}