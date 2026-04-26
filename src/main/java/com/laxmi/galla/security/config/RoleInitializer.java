package com.laxmi.galla.security.config;

import com.laxmi.galla.core.security.repository.RoleRepository;
import com.laxmi.galla.security.entity.RoleEntity;
import com.laxmi.galla.security.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        // Loop through all enum roles
        for (Role role : EnumSet.allOf(Role.class)) {
            roleRepository.findByRole(role).ifPresentOrElse(
                    existing -> log.debug("Role {} already exists with id {}", role, existing.getId()),
                    () -> {
                        RoleEntity entity = new RoleEntity();
                        entity.setRole(role);
                        roleRepository.save(entity);
                        log.info("Role {} created successfully", role);
                    }
            );
        }

        log.info("Role seeding finished. Total roles in DB: {}", roleRepository.count());
    }
}