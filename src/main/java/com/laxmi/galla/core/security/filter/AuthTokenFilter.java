package com.laxmi.galla.core.security.filter;

import com.laxmi.galla.core.security.service.CoreUserDetailsService;
import com.laxmi.galla.core.security.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Core JWT Authentication Filter – stateless, secure, and reusable.
 *
 * Responsibilities (narrow & strict):
 *   • Extract JWT token (Bearer header preferred, cookie fallback optional)
 *   • Validate signature & expiration via JwtUtils
 *   • Load UserDetails (must be cached for performance)
 *   • Set Authentication in SecurityContext when valid
 *
 * Invariants (never broken):
 *   • NEVER short-circuits the filter chain
 *   • Always calls filterChain.doFilter(...) exactly once in main flow
 *   • Does NOT make authorization decisions
 *   • Does NOT contain business rules (soft-delete, vendor status, etc.)
 *   • Does NOT depend on domain models
 *
 * Public endpoints must be permitAll() in SecurityFilterChain — not here.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String COOKIE_NAME = "access_token";

    private final JwtUtils jwtUtils;
    private final CoreUserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Only skip noisy documentation paths
        // /api/auth/** must be permitAll() in SecurityFilterChain
        return path != null &&
                (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Already authenticated (dispatcher forward/include, error handling, etc.)
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            if (log.isTraceEnabled()) {
                log.trace("SecurityContext already contains authentication – skipping JWT processing");
            }
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = extractToken(request);

            // No token → anonymous / public request
            if (!StringUtils.hasText(token)) {
                if (log.isTraceEnabled()) {
                    log.trace("No JWT token present for {}", request.getRequestURI());
                }
                filterChain.doFilter(request, response);
                return;
            }

            // Invalid token → proceed as unauthenticated
            String email;
            try {
                jwtUtils.validateAccessToken(token);
                email = jwtUtils.getEmailFromToken(token);
            } catch (JwtUtils.JwtValidationException ex) {
                if (log.isDebugEnabled()) {
                    log.debug("Invalid or expired JWT token for {}: {}", request.getRequestURI(), ex.getMessage());
                }
                filterChain.doFilter(request, response);
                return;
            }

            // Extract subject (username/email)
            String username = jwtUtils.getEmailFromToken(token);
            if (!StringUtils.hasText(username)) {
                if (log.isDebugEnabled()) {
                    log.debug("JWT token missing or empty subject claim");
                }
                filterChain.doFilter(request, response);
                return;
            }

            // ── Happy path ────────────────────────────────────────────────────────
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Future extension point: revocation / rotation / JTI blacklist / IP check
            // if (shouldInvalidateToken(token, userDetails, request)) {
            //     log.debug("Token invalidated for security reason");
            //     filterChain.doFilter(request, response);
            //     return;
            // }

            UsernamePasswordAuthenticationToken authentication =
                    UsernamePasswordAuthenticationToken.authenticated(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (log.isDebugEnabled()) {
                log.debug("Successfully authenticated user: {} for path: {}", username, request.getRequestURI());
            }

        } catch (UsernameNotFoundException e) {
            SecurityContextHolder.clearContext();
            log.debug("User not found for token subject: {}", e.getMessage());
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException e) {
            SecurityContextHolder.clearContext();
            if (log.isDebugEnabled()) {
                log.debug("Invalid JWT token: {}", e.getMessage());
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            log.warn("Unexpected error during JWT authentication", e);
        }

        // Single, guaranteed continuation point
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        // 1. Bearer Authorization header – preferred & most secure
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            return header.substring(TOKEN_PREFIX.length()).trim();
        }

        // 2. Cookie fallback – only use if explicitly needed (rare in strict APIs)
        // Production security requirements:
        //   • HttpOnly = true
        //   • Secure = true (HTTPS only)
        //   • SameSite = Strict or Lax (Strict preferred for APIs)
        //   • Path = /
        //   • Domain = correctly set
        // Configure these when creating the cookie (login/refresh endpoints)
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(c -> COOKIE_NAME.equals(c.getName()))
                        .map(Cookie::getValue)
                        .map(String::trim)
                        .filter(StringUtils::hasText)
                        .findFirst())
                .orElse(null);
    }
}