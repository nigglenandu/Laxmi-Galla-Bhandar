package com.laxmi.galla.security.service;

import com.laxmi.galla.core.security.CustomUserDetails;
import com.laxmi.galla.core.security.service.UserDetailsLoader;
import com.laxmi.galla.entity.User;
import com.laxmi.galla.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("domainUserDetailsLoader")
public class DomainUserDetailsLoader implements UserDetailsLoader {

    private final UserRepository userRepository;

    public DomainUserDetailsLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CustomUserDetails loadUser(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<SimpleGrantedAuthority> authorities =
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole().name()))
                        .toList();

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.isActive() && user.isEmailVerified(),
                authorities
        );
    }
}