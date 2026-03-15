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
import org.springframework.security.core.Authentication;
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

    public ApiResult<Map<String, String>> signup(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.email())) {
            throw new BusinessException("Email is already taken", "EMAIL_TAKEN");
        }

        // Encode password securely using PasswordEncoder
        String encodedPassword = passwordEncoder.encode(signupRequest.password());

        User user = User.builder()
                .firstName(signupRequest.firstName())
                .lastName(signupRequest.lastName())
                .email(signupRequest.email())
                .password(encodedPassword) // <-- updated here
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

    @Override
    public ApiResult<AuthResponse> login(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {

        // Fetch user by email
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new BusinessException(
                        "Invalid email or password",
                        "INVALID_CREDENTIALS",
                        HttpStatus.UNAUTHORIZED
                ));

        // Verify password
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new BusinessException(
                    "Invalid email or password",
                    "INVALID_CREDENTIALS",
                    HttpStatus.UNAUTHORIZED
            );
        }

        // Claims for JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());
        claims.put("fn", user.getFirstName());
        claims.put("ln", user.getLastName());
        claims.put("uid", user.getId());

        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        List<String> roleNames = user.getRoles().stream()
                .map(roleEntity -> roleEntity.getRole().name())
                .sorted()
                .toList();

        // Generate JWT tokens
        var tokenPair = tokenService.generateTokens(
                user.getEmail(),
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

        // Calculate expiration
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
    public ApiResult<Void> logout(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            tokenService.clearTokens(response);
            return ApiResult.<Void>builder()
                    .success(true)
                    .message("Already logged out")
                    .httpStatus(HttpStatus.OK)
                    .build();
        }

        String email = auth.getName();
        tokenService.revokeAllForSubject(email);
        tokenService.clearTokens(response);

        log.info("User logged out successfully | email={} | ip={} | ua={}",
                email,
                request.getRemoteAddr(),
                request.getHeader("User-Agent"));

        return ApiResult.ok(null, "Logout successful");
    }
}