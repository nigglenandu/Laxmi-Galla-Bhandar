package com.laxmi.galla.security.controller;

import com.laxmi.galla.core.dto.response.ApiResult;
import com.laxmi.galla.core.dto.response.TokenPair;
import com.laxmi.galla.core.exception.TokenReuseDetectedException;
import com.laxmi.galla.core.exception.TokenValidationException;
import com.laxmi.galla.core.model.RefreshToken;
import com.laxmi.galla.core.security.repository.RefreshTokenRepository;
import com.laxmi.galla.core.security.service.ITokenService;
import com.laxmi.galla.core.security.utils.HashUtils;
import com.laxmi.galla.entity.User;
import com.laxmi.galla.repository.UserRepository;
import com.laxmi.galla.security.dto.request.RefreshRequest;
import com.laxmi.galla.security.dto.response.AuthResponse;
import com.laxmi.galla.security.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RefreshTokenController {

    private final IAuthService authService;
    private final ITokenService tokenService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${platform.security.jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${platform.security.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @PostMapping("/refresh")
    public ResponseEntity<ApiResult<AuthResponse>> refresh(
            @Valid @RequestBody(required = false) RefreshRequest bodyRequest,
            HttpServletRequest request,
            HttpServletResponse response) {

        // 1️⃣ Extract token: header > cookie > body fallback
        String refreshToken = authService.extractRefreshToken(request);
        if (!StringUtils.hasText(refreshToken) && bodyRequest != null) {
            refreshToken = bodyRequest.refreshToken();
        }

        if (!StringUtils.hasText(refreshToken)) {
            return ApiResult.<AuthResponse>badRequestT(
                    "Refresh token required (body, header, cookie or Authorization)",
                    "REFRESH_TOKEN_REQUIRED"
            ).toResponseEntity();
        }

        try {
            // 2️⃣ Validate & find refresh token
            String hash = HashUtils.sha256Base64(refreshToken);
            RefreshToken oldToken = refreshTokenRepository.findByTokenHash(hash)
                    .orElseThrow(() -> new TokenValidationException("Refresh token not found"));

            // 3️⃣ Load fresh user claims
            User user = userRepository.findByEmail(oldToken.getSubject())
                    .orElseThrow(() -> new TokenValidationException("User not found"));

            Map<String, Object> freshClaims = Map.of(
                    "roles", user.getRoles().stream()
                            .map(r -> r.getRole().name())
                            .collect(Collectors.toList()),
                    "fn", user.getFirstName(),
                    "ln", user.getLastName(),
                    "uid", user.getId()
            );

            // 4️⃣ Rotate refresh token securely
            TokenPair newPair = tokenService.rotateRefreshToken(refreshToken, freshClaims);

            // 5️⃣ Update HttpOnly cookies
            tokenService.addTokensToResponse(response, newPair.accessToken(), newPair.refreshToken());

            // 6️⃣ Build API response
            Instant now = Instant.now();
            long expiresInSec = accessTokenExpirationMs / 1000L;

            AuthResponse authResponse = new AuthResponse(
                    newPair.accessToken(),
                    newPair.refreshToken(),
                    now.plusMillis(refreshTokenExpirationMs),
                    expiresInSec,
                    now.plusMillis(accessTokenExpirationMs),
                    "Bearer",
                    now,
                    user.getEmail(),
                    user.getRoles().stream()
                            .map(r -> r.getRole().name())
                            .collect(Collectors.toList())
            );

            return ApiResult.ok(authResponse, "Tokens refreshed successfully")
                    .toResponseEntity();

        } catch (TokenReuseDetectedException e) {
            // ⚠️ Security incident: revoke all user sessions
            tokenService.revokeAllForSubject(e.getSubject());
            log.warn("SECURITY INCIDENT: Token reuse detected during refresh | subject={} | jti={} | traceId={}",
                    e.getSubject(), e.getJti(), MDC.get("traceId"), e);

            return ApiResult.<AuthResponse>errorT(HttpStatus.FORBIDDEN,
                            "Token reuse detected – all sessions terminated",
                            "TOKEN_REUSE_DETECTED",
                            null, null, request.getRequestURI(), null)
                    .toResponseEntity();

        } catch (TokenValidationException e) {
            return ApiResult.<AuthResponse>unauthorizedT("Invalid or expired refresh token", "INVALID_REFRESH_TOKEN")
                    .toResponseEntity();

        } catch (Exception e) {
            log.error("Unexpected error during token refresh", e);
            return ApiResult.<AuthResponse>errorT(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Token refresh failed – please try again later",
                            "INTERNAL_ERROR",
                            null, null, request.getRequestURI(), null)
                    .toResponseEntity();
        }
    }

    @PostMapping("/revoke")
    public ResponseEntity<ApiResult<Void>> revokeToken(
            HttpServletRequest request,
            HttpServletResponse response) {

        String refreshToken = authService.extractRefreshToken(request);

        if (!StringUtils.hasText(refreshToken)) {
            return ApiResult.badRequest(
                    "Refresh token required",
                    "REFRESH_TOKEN_REQUIRED"
            ).toResponseEntity();
        }

        tokenService.revokeRefreshToken(refreshToken);
        tokenService.clearTokens(response);

        return ApiResult.noContent().toResponseEntity();
    }
}
