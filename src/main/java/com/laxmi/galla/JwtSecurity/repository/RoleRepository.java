package com.laxmi.galla.JwtSecurity.repository;

import Niggle.Nandu.Jwt.Security.JwtSecurity.model.Role;
import Niggle.Nandu.Jwt.Security.JwtSecurity.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
        Optional<RoleEntity> findByRole(Role role);
    }
