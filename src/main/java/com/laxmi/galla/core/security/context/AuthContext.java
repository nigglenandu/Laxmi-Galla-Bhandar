package com.laxmi.galla.core.security.context;

import com.laxmi.galla.core.security.CustomUserDetails;
import org.slf4j.MDC;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuthContext {

    private static final String CORRELATION_ID_KEY = "correlationId";

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

    /**
     * Returns correlation ID for distributed tracing.
     * Falls back to generating a new one if none exists.
     */
    public String getCorrelationId() {
        return Optional.ofNullable(MDC.get(CORRELATION_ID_KEY))
                .orElseGet(() -> {
                    String newId = UUID.randomUUID().toString();
                    MDC.put(CORRELATION_ID_KEY, newId);
                    return newId;
                });
    }
}