package com.laxmi.galla.core.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Caching decorator – created conditionally by config.
 */
@RequiredArgsConstructor
public class CachedUserDetailsLoader implements UserDetailsLoader {

    private final UserDetailsLoader delegate;

    @Cacheable(value = "userDetails", key = "#username", unless = "#result == null")
    @Override
    public UserDetails loadUser(String username) throws UsernameNotFoundException {
        return delegate.loadUser(username);
    }
}