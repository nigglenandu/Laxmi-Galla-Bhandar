package com.laxmi.galla.core.config;

import com.laxmi.galla.core.security.CustomUserDetails;
import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditAware")
public class AuditorAwareImpl implements AuditorAware<Long> {

    private static final Long SYSTEM_ID = 0L;

    @Override
    public @NonNull Optional<Long> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken || "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.of(SYSTEM_ID);
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetails details) {
            return Optional.of(details.getId());
        }

        return Optional.of(SYSTEM_ID);
    }
}