package com.laxmi.galla.JwtSecurity.repository;

import com.laxmi.galla.JwtSecurity.model.AuthUserEntity;
import com.laxmi.galla.JwtSecurity.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void deleteAllByUser(AuthUserEntity user);
}
