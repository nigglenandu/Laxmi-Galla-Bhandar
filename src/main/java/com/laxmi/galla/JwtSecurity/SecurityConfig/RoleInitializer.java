package com.laxmi.galla.JwtSecurity.SecurityConfig;

import Niggle.Nandu.Jwt.Security.JwtSecurity.model.Role;
import Niggle.Nandu.Jwt.Security.JwtSecurity.model.RoleEntity;
import Niggle.Nandu.Jwt.Security.JwtSecurity.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RoleInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        List<Role> roles = Arrays.asList(
                Role.ROLE_USER,
                Role.ROLE_ADMIN,
                Role.ROLE_VENDOR,
                Role.ROLE_DELIVERY,
                Role.ROLE_SUPPORT
        );

        for (Role role : roles) {
            if (!roleRepository.findByRole(role).isPresent()) {
                RoleEntity roleEntity = new RoleEntity();
                roleEntity.setRole(role);
                roleRepository.save(roleEntity);
            }
        }
    }
}
