package com.laxmi.galla.core.security.repository;


import com.laxmi.galla.security.entity.RoleEntity;
import com.laxmi.galla.security.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRole(Role role);

}