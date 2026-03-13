package core.security.config;

import core.security.exception.AuthEntryPointJwt;
import core.security.filter.AuthTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Core / platform-level Spring Security infrastructure configuration.
 *
 * Configures ONLY foundational security concerns:
 *   - Stateless session management
 *   - CSRF disabled (safe for token-based auth)
 *   - JWT Bearer authentication filter
 *   - Centralized 401/403 handling
 *   - Production-safe CORS (configurable via properties)
 *   - Strong password encoding
 *   - Authentication manager
 *
 * IMPORTANT – THIS CONFIG DOES NOT:
 *   • Contain ANY business authorization rules (.authorizeHttpRequests matchers)
 *   • Know about ANY endpoints (/api/auth/**, /swagger-ui/**, /admin/**, etc.)
 *   • Depend on domain models or application-specific logic
 *
 * Applications MUST provide their own SecurityFilterChain (or extend this one)
 * to define public endpoints, role-based access, OAuth2 handlers, etc.
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(CorsProperties.class)
@RequiredArgsConstructor
public class CoreSecurityConfiguration {

    private final AuthEntryPointJwt unauthorizedHandler;
    private final AuthTokenFilter authTokenFilter;
    private final CorsProperties corsProperties;

    @Bean
    public SecurityFilterChain coreSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS – fully configured via properties
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Disable CSRF – safe for stateless JWT/OAuth2 APIs
                .csrf(csrf -> csrf.disable())

                // Stateless – no server-side sessions
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Centralized exception handling (401/403)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))

                // Core authentication: JWT Bearer filter
                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        // No authorization rules here — leave to applications
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Strength 12 = strong security with acceptable performance
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(corsProperties.isAllowCredentials());
        config.setAllowedOriginPatterns(corsProperties.getAllowedOriginPatterns());
        config.setAllowedMethods(corsProperties.getAllowedMethods());
        config.setAllowedHeaders(corsProperties.getAllowedHeaders());
        config.setExposedHeaders(corsProperties.getExposedHeaders());
        config.setMaxAge(corsProperties.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}