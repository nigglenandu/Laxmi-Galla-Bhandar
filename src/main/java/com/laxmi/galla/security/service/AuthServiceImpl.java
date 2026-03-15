package com.laxmi.galla.security.service;


import com.laxmi.galla.entity.User;
import com.laxmi.galla.repository.UserRepository;
import com.laxmi.galla.security.dto.request.LoginRequest;
import com.laxmi.galla.security.dto.request.SignupRequest;
import com.laxmi.galla.security.dto.response.AuthResponse;
import com.laxmi.galla.security.entity.RoleEntity;
import com.laxmi.galla.security.enums.OtpPurpose;
import com.laxmi.galla.security.enums.Role;
import com.laxmi.galla.core.dto.response.ApiResult;
import com.laxmi.galla.core.exception.BusinessException;
import com.laxmi.galla.core.security.repository.RoleRepository;
import com.laxmi.galla.core.security.service.ITokenService;
import com.laxmi.galla.core.security.utils.HashUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {

    private final ITokenService tokenService;
    // TODO: Replace with your actual UserRepository or AuthRepository
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final IEmailService emailService;


    @Value("${platform.security.jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${platform.security.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    /**
     * Signup – simple example, stores user and returns minimal info.
     */
    public ApiResult<Map<String, String>> signup(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.email())) {
            throw new BusinessException("Email is already taken", "EMAIL_TAKEN");
        }

        User user = User.builder()
                .firstName(signupRequest.firstName())
                .lastName(signupRequest.lastName())
                .email(signupRequest.email())
                .password(HashUtils.sha256Base64(signupRequest.password()))
                .active(true)
                .emailVerified(false)
                .roles(new HashSet<>())
                .build();

        Set<RoleEntity> roles = new HashSet<>();

        if (signupRequest.roles() == null || signupRequest.roles().isEmpty()) {
            // Default role assignment
            RoleEntity defaultRole = roleRepository.findByRole(Role.ADMIN)
                    .orElseThrow(() -> new BusinessException(
                            "Default role ADMIN not found", "ROLE_NOT_FOUND"));
            roles.add(defaultRole);
            log.info("No roles sent — assigning default role ADMIN");
        } else {
            // Map requested role names to RoleEntity
            roles = signupRequest.roles().stream()
                    .map(roleName -> {
                        try {
                            // Convert string to enum safely
                            Role roleEnum = Role.valueOf(roleName.toUpperCase());

                            // Check if role exists in DB
                            return roleRepository.findByRole(roleEnum)
                                    .orElseThrow(() -> new BusinessException(
                                            "Role " + roleName + " not found", "ROLE_NOT_FOUND"));
                        } catch (IllegalArgumentException ex) {
                            // Invalid enum string
                            throw new BusinessException(
                                    "Invalid role: " + roleName, "INVALID_ROLE");
                        }
                    })
                    .collect(Collectors.toSet());
        }

        user.setRoles(roles);
        userRepository.save(user);

        String otp = otpService.generateOtp(user.getEmail(), OtpPurpose.SIGNUP_VERIFICATION);
        emailService.sendOtp(user.getEmail(), otp);

        Map<String, String> result = Map.of(
                "userId", user.getId().toString()
        );

        return ApiResult.<Map<String, String>>created(result)
                .toBuilder()
                .message("Signup successful")
                .build();
    }

    /**
     * Login – validates credentials, issues JWTs, sets cookies.
     */
    @Override
    public ApiResult<AuthResponse> login(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {

        Optional<User> userOtp = userRepository.findByEmail(loginRequest.email());

                if(userOtp.isEmpty() || !passwordEncoder.matches(loginRequest.password(), userOtp.get().getPassword())) {
                    throw new BusinessException(
                            "Invalid email or password",
                            "INVALID_CREDENTIALS",
                            HttpStatus.UNAUTHORIZED
                    );
                }
                User user = userOtp.get(); 

        // ── FIX: Use proper password encoder (BCrypt, Argon2, etc.)
        // Assuming you have PasswordEncoder injected as field: private final PasswordEncoder passwordEncoder;
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new BusinessException(
                    "Invalid email or password",
                    "INVALID_CREDENTIALS",
                    HttpStatus.UNAUTHORIZED
            );
        }

        // Claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());
        claims.put("fn", user.getFirstName());
        claims.put("ln", user.getLastName());
        claims.put("uid", user.getId());

        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        List<String> roleNames = user.getRoles().stream()
                .map(roleEntity -> roleEntity.getRole().name())   // → "USER", "ADMIN", ...
                .sorted()                                         // optional: nice for consistency
                .toList();

        // No fingerprint anymore – assuming signature is now only 4 params
        var tokenPair = tokenService.generateTokens(
                user.getEmail(),    // subject
                claims,
                ip,
                userAgent,
                null
        );

        tokenService.addTokensToResponse(
                response,
                tokenPair.accessToken(),
                tokenPair.refreshToken()
        );

        // ── Expiry calculation (inject these values!)
        // Example: use @Value in this class or parent
        // @Value("${jwt.access.expiration-ms:900000}")   // 15 min default
        // private long accessTokenExpirationMs;
        //
        // @Value("${jwt.refresh.expiration-ms:604800000}") // 7 days default
        // private long refreshTokenExpirationMs;

        Instant now = Instant.now();
        long expiresInSeconds = accessTokenExpirationMs / 1000;
        Instant expiresAt = now.plusMillis(accessTokenExpirationMs);
        Instant refreshExpiresAt = now.plusMillis(refreshTokenExpirationMs);

        AuthResponse authResponse = new AuthResponse(
                tokenPair.accessToken(),
                tokenPair.refreshToken(),
                refreshExpiresAt,
                expiresInSeconds,
                expiresAt,
                "Bearer",
                now,
                user.getEmail(),
                roleNames
        );

        return ApiResult.ok(authResponse);
    }
    /**
     * Logout – clears cookies and optionally revokes refresh tokens.
     */
    @Override
    public ApiResult<Void> logout(HttpServletRequest request,
                                  HttpServletResponse response,
                                  @AuthenticationPrincipal String subject) {  // or UserDetails / Jwt principal

        if (!StringUtils.hasText(subject)) {
            // No authenticated user → just clear cookies and return
            tokenService.clearTokens(response);
            return ApiResult.<Void>builder()
                    .success(true)
                    .message("Already logged out")
                    .httpStatus(HttpStatus.OK)
                    .build();
        }

        // Revoke ALL refresh tokens for this user (logs out from all devices)
        tokenService.revokeAllForSubject(subject);

        // Clear access & refresh cookies
        tokenService.clearTokens(response);

        // Optional: log for audit
        log.info("User logged out successfully | subject={} | ip={} | ua={}",
                subject,
                request.getRemoteAddr(),
                request.getHeader("User-Agent"));

        return ApiResult.ok(null, "Logout successful");
    }
}