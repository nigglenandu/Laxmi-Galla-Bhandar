package com.laxmi.galla.core.security.context;

import com.laxmi.galla.core.security.CustomUserDetails;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthContext {

    public CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null ||
                !auth.isAuthenticated() ||
                auth instanceof AnonymousAuthenticationToken ||
                !(auth.getPrincipal() instanceof CustomUserDetails user)) {
            throw new IllegalStateException("No authenticated user found");
        }

        return user;
    }

    public Long getUserId() {
        return getCurrentUser().getId();
    }

    public String getEmail() {
        return getCurrentUser().getUsername();
    }

    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);
    }

    public boolean hasAuthority(String authority) {
        return getCurrentUser()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }
}