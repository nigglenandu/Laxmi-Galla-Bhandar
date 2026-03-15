package com.laxmi.galla.core.security.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Core (shared) UserDetailsService that delegates to a pluggable loader.
 *
 * Features:
 * - Input validation
 * - Masked logging (privacy)
 * - Trace ID support (MDC)
 * - Audit logging
 * - Micrometer metrics (success, not_found, error + latency)
 */
@Service
@Lazy
@RequiredArgsConstructor
public class CoreUserDetailsService implements UserDetailsService {
    private final UserDetailsLoader userDetailsLoader;
    private final MeterRegistry meterRegistry;
    private static final Logger auditLogger = LoggerFactory.getLogger("security.audit");
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Validate input
        if (username == null || username.isBlank()) {
            auditLogger.warn("Invalid username attempt: null or blank");
            throw new UsernameNotFoundException("Username cannot be blank");
        }
        // 2. Mask for logs
        String maskedUsername = maskEmail(username);
        // 3. Trace ID for distributed logs
        String traceId = MDC.get("traceId") != null ? MDC.get("traceId") : "N/A";
        auditLogger.info("UserDetails load attempt | username={} | traceId={}", maskedUsername, traceId);
        // 4. Metrics start
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            // 5. Delegate actual loading
            UserDetails user = userDetailsLoader.loadUser(username);
            // 6. Success logging & metrics
            auditLogger.info("UserDetails load success | username={} | traceId={}", maskedUsername, traceId);
            sample.stop(meterRegistry.timer("security.userdetails.load", "outcome", "success"));
            return user;
        } catch (UsernameNotFoundException e) {
            auditLogger.warn("UserDetails load not found | username={} | traceId={}", maskedUsername, traceId);
            sample.stop(meterRegistry.timer("security.userdetails.load", "outcome", "not_found"));
            throw e;
        } catch (Exception e) {
            auditLogger.error("UserDetails load error | username={} | traceId={}", maskedUsername, traceId, e);
            sample.stop(meterRegistry.timer("security.userdetails.load", "outcome", "error"));
            throw e;
        }
    }
    // Utility: Mask email for logs
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "unknown";
        String[] parts = email.split("@");
        String local = parts[0];
        return local.length() <= 3 ? "***@" + parts[1] : local.substring(0, 3) + "****@" + parts[1];
    }
}