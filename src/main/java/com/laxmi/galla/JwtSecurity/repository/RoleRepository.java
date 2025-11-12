package com.laxmi.galla.JwtSecurity.repository;

import com.laxmi.galla.JwtSecurity.model.Role;
import com.laxmi.galla.JwtSecurity.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
        Optional<RoleEntity> findByRole(Role role);
    }
