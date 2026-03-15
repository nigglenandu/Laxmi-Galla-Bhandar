package com.laxmi.galla.core.security.config;

import com.laxmi.galla.core.security.service.CachedUserDetailsLoader;
import com.laxmi.galla.core.security.service.UserDetailsLoader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Conditional caching for user details loading.
 * Active only when platform.security.user-details.cache.enabled=true
 */
@Configuration
public class UserDetailsLoaderConfig {

    @Bean
    @Primary
    @ConditionalOnBean(name = "domainUserDetailsLoader")
    @ConditionalOnProperty(
            name = "platform.security.user-details.cache.enabled",
            havingValue = "true",
            matchIfMissing = false
    )
    public UserDetailsLoader cachedUserDetailsLoader(
            @Qualifier("domainUserDetailsLoader") UserDetailsLoader delegate) {
        return new CachedUserDetailsLoader(delegate);
    }

    @Bean
    @ConditionalOnMissingBean(UserDetailsLoader.class)
    public UserDetailsLoader fallbackUserDetailsLoader() {
        return username -> {
            throw new UsernameNotFoundException(
                    "No UserDetailsLoader bean configured. " +
                    "Define a bean named 'domainUserDetailsLoader' implementing UserDetailsLoader."
            );
        };
    }
}
