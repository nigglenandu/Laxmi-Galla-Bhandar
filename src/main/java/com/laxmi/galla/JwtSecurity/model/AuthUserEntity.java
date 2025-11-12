package com.laxmi.galla.JwtSecurity.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class AuthUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles = new HashSet<>();

    public AuthUserEntity() {
    }

    public AuthUserEntity(long userId, String username, String password, String email, Set<RoleEntity> roles) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles != null ? roles : new HashSet<>();
    }

    public long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles != null ? roles : new HashSet<>();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AuthUserEntity.class.getSimpleName() + "[", "]")
                .add("userId=" + userId)
                .add("username='" + username + "'")
                .add("password='" + password + "'")
                .add("email='" + email + "'")
                .add("roles=" + roles)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long userId;
        private String username;
        private String password;
        private String email;
        private Set<RoleEntity> roles;

        private Builder() {
            this.roles = new HashSet<>();
        }

        public Builder userId(long userId) {
            this.userId = userId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder roles(Set<RoleEntity> roles) {
            this.roles = roles != null ? roles : new HashSet<>();
            return this;
        }

        public AuthUserEntity build() {
            return new AuthUserEntity(userId, username, password, email, roles);
        }
    }
}