package com.laxmi.galla.security.service;


import com.laxmi.galla.domain.entity.User;
import com.laxmi.galla.repository.UserRepository;
import com.laxmi.galla.security.dto.request.LoginRequest;
import com.laxmi.galla.security.dto.request.OtpVerifyRequest;
import com.laxmi.galla.security.dto.request.SignupRequest;
import com.laxmi.galla.security.dto.response.AuthResponse;
import com.laxmi.galla.security.dto.response.OtpVerificationResult;
import com.laxmi.galla.security.entity.RoleEntity;
import com.laxmi.galla.security.enums.OtpPurpose;
import com.laxmi.galla.security.enums.Role;
import com.laxmi.galla.core.dto.response.ApiResult;
import com.laxmi.galla.core.exception.BusinessException;
import com.laxmi.galla.core.security.repository.RoleRepository;
import com.laxmi.galla.core.security.service.ITokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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
        String email = signupRequest.email().trim().toLowerCase();

        Optional<User> existingOpt = userRepository.findByEmail(email);  // better than existsByEmail (can reuse user)

        User user;
        boolean isNewUser = false;

        if (existingOpt.isPresent()) {
            user = existingOpt.get();
            if (user.isEmailVerified()) {
                throw new BusinessException("Email is already registered and verified. Please login.", "EMAIL_TAKEN", HttpStatus.CONFLICT);
            }
            // Unverified → resend OTP (no new creation)
            log.info("Resending verification OTP for existing unverified user: {}", email);
        } else {
            // New user
            String encodedPassword = passwordEncoder.encode(signupRequest.password());

            user = User.builder()
                    .firstName(signupRequest.firstName())
                    .lastName(signupRequest.lastName())
                    .phoneNumber(signupRequest.phoneNumber())
                    .email(email)
                    .password(encodedPassword)
                    .accountLocked(true)
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
            isNewUser = true;

        }

        // Always send fresh OTP (invalidates old one via your otpService)
        String otp = otpService.generateOtp(email, OtpPurpose.SIGNUP_VERIFICATION);
        emailService.sendOtp(email, otp);

        Map<String, String> result = Map.of(
                "userId", user.getId().toString(),
                "emailVerified", "false",
                "otpSent", "true",
                "message", isNewUser ? "Signup successful - verify your email" : "Verification OTP resent"
        );

        return ApiResult.<Map<String, String>>created(result)
                .toBuilder()
                .build();
    }


    // Step 1: Password → send OTP
    @Override
    public ApiResult<Void> initiateLogin(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new BusinessException("Invalid credentials", "INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new BusinessException("Invalid credentials", "INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED);
        }

        OtpPurpose purpose = user.isEmailVerified()
                ? OtpPurpose.LOGIN   // ← use consistent name, not ADMIN_LOGIN unless really admin-only
                : OtpPurpose.SIGNUP_VERIFICATION;

        String message = user.isEmailVerified()
                ? "OTP sent to your email. Enter it to complete login."
                : "Account not verified. Verification OTP sent - please verify first.";

        String otp = otpService.generateOtp(user.getEmail(), purpose);
        emailService.sendOtp(user.getEmail(), otp);

        // Optional: store ip/ua hash in OTP metadata for extra security

        throw new BusinessException(message, purpose.name(), HttpStatus.ACCEPTED);
    }

    @Override
    public ApiResult<AuthResponse> completeLoginWithOtp(String email, HttpServletRequest
            request, HttpServletResponse response) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found", "NOT_FOUND", HttpStatus.GONE));

        // Claims for JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles().stream()
                .map(role -> role.getRole().name())
                .toList());
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


    public ApiResult<Void> logout(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Authentication auth) {

        String refreshToken = extractRefreshToken(request);

        if (StringUtils.hasText(refreshToken)) {
            tokenService.revokeRefreshToken(refreshToken);
        }

        tokenService.clearTokens(response);

        if (auth != null && auth.isAuthenticated()) {
            log.info("User logged out | user={} | ip={} | ua={}",
                    auth.getName(),
                    request.getRemoteAddr(),
                    request.getHeader("User-Agent"));
        }

        return ApiResult.ok(null, "Logout successful");
    }

    @Transactional
    public OtpVerificationResult verifyOtp(
            OtpVerifyRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        OtpPurpose purpose = parsePurpose(request.purpose());

        log.info("OTP verification | email={} purpose={}",
                request.email(), purpose);

        otpService.verifyOtp(request.email(), request.otp(), purpose);

        return switch (purpose) {

            case SIGNUP_VERIFICATION -> {
                markEmailVerified(request.email());
                yield new OtpVerificationResult(purpose, null);
            }

            case LOGIN -> {
                AuthResponse auth = completeLoginWithOtp(
                        request.email(), httpRequest, httpResponse).getData();
                yield new OtpVerificationResult(purpose, auth);
            }

            default -> throw new BusinessException(
                    "Unsupported OTP purpose",
                    "UNSUPPORTED_PURPOSE",
                    HttpStatus.BAD_REQUEST
            );
        };
    }

    private OtpPurpose parsePurpose(String purpose) {
        try {
            return OtpPurpose.valueOf(purpose.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(
                    "Invalid OTP purpose",
                    "INVALID_PURPOSE",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public String extractRefreshToken(HttpServletRequest request) {
        // 1. Prefer custom header (mobile, Postman, etc.)
        String token = request.getHeader("X-Refresh-Token");
        if (StringUtils.hasText(token)) {
            return token.trim();
        }

        // 2. Fallback to cookie (web browsers)
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName()) && StringUtils.hasText(cookie.getValue())) {
                    return cookie.getValue().trim();
                }
            }
        }

        // 3. Optional: future-proof — check Authorization header (Bearer style)
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7).trim();
        }

        return null;
    }

    public void markEmailVerified(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    if (!user.isEmailVerified()) {
                        user.setEmailVerified(true);
                        userRepository.save(user);
                    }
                });
    }

}