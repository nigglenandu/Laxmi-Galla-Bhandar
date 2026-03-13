package com.laxmi.galla.entity;

import com.laxmi.galla.security.entity.RoleEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    private boolean isEmailVerified = false;

    @Column(nullable = false)
    private boolean active = true;

    public AuthUserEntity(boolean active, String email, boolean isEmailVerified, String password, Set<RoleEntity> roles, long userId, String username) {
        this.active = active;
        this.email = email;
        this.isEmailVerified = isEmailVerified;
        this.password = password;
        this.roles = roles;
        this.userId = userId;
        this.username = username;
    }

    public AuthUserEntity() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Builder class
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private long userId;
        private String username;
        private String password;
        private String email;
        private Set<RoleEntity> roles = new HashSet<>();
        private boolean isEmailVerified = false;
        private boolean active = true;

        public Builder userId(long userId) { this.userId = userId; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder roles(Set<RoleEntity> roles) { this.roles = roles; return this; }
        public Builder isEmailVerified(boolean isEmailVerified) { this.isEmailVerified = isEmailVerified; return this; }
        public Builder active(boolean active) { this.active = active; return this; } // new setter

        public AuthUserEntity build() {
            return new AuthUserEntity(
                    true,          // active
                    email,
                    isEmailVerified,
                    password,
                    roles,
                    userId,
                    username
            );
        }

    }

    @Override
    public String toString() {
        return "AuthUserEntity{" +
                "email='" + email + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + roles +
                ", isEmailVerified=" + isEmailVerified +
                '}';
    }
}
