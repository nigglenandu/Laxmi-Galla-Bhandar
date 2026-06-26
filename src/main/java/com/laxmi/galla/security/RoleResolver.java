package com.laxmi.galla.security;

import com.laxmi.galla.security.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class RoleResolver {

    public Role resolve(Collection<? extends GrantedAuthority> authorities) {

        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .map(Role::valueOf)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Role not found"));
    }
}