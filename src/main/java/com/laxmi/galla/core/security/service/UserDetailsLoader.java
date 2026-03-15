package com.laxmi.galla.core.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Pluggable contract for loading UserDetails.
 *
 * Implemented in each service/module (auth/user service) with domain-specific logic.
 * Core never knows about User entity, VendorEntity, isApproved, etc.
 */
@FunctionalInterface
public interface UserDetailsLoader {

    /**
     * Load UserDetails by username (usually email or identifier).
     *
     * Responsibilities of implementation:
     * - Fetch user from database, LDAP, external IdP, etc.
     * - Throw UsernameNotFoundException if not found
     * - Apply business rules (enabled, deleted, approved, locked, tenant check, etc.)
     * - Build and return CustomUserDetails (or compatible UserDetails)
     */
    UserDetails loadUser(String username) throws UsernameNotFoundException;
}