package com.laxmi.galla.JwtSecurity.service;

import com.laxmi.galla.JwtSecurity.model.AuthUserEntity;
import com.laxmi.galla.JwtSecurity.model.RefreshToken;
import com.laxmi.galla.JwtSecurity.payload.LoginRequest;
import com.laxmi.galla.JwtSecurity.payload.SignupRequest;
import com.laxmi.galla.JwtSecurity.repository.RefreshTokenRepository;
import com.laxmi.galla.JwtSecurity.repository.UserRepository;
import com.laxmi.galla.JwtSecurity.security.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${spring.app.jwtRefreshExpirationMs}")
    private long refreshMs;

    @Value("${app.cookie.secure:true}")
    private boolean cookieSecure;

    @Value("${app.cookie.sameSite:Strict}")
    private String sameSite;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       JwtUtils jwtUtils,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void register(SignupRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) throw new RuntimeException("Username exists");
        if (userRepository.existsByEmail(req.getEmail())) throw new RuntimeException("Email exists");

        AuthUserEntity u = AuthUserEntity.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .build();

        userRepository.save(u);

        emailService.sendVerification(u);
    }

    public LoginResult login(LoginRequest req, HttpServletRequest servletRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtUtils.generateAccessToken(userDetails);
        String refreshTokenPlain = generateSecureToken(64);

        RefreshToken rt = RefreshToken.builder()
                .tokenHash(sha256(refreshTokenPlain))
                .user(userRepository.findByUsername(userDetails.getUsername()).orElseThrow())
                .expiryDate(Instant.now().plusMillis(refreshMs))
                .userAgent(servletRequest.getHeader("User-Agent"))
                .ipAddress(servletRequest.getRemoteAddr())
                .build();
        refreshTokenRepository.save(rt);

        return new LoginResult(accessToken, refreshTokenPlain, userDetails);
    }

    public RefreshResult refresh(HttpServletRequest request) {
        var cookie = getCookie(request, "refresh_token");
        if (cookie == null) return null;

        String refreshToken = cookie.getValue();
        String hashed = sha256(refreshToken);

        Optional<RefreshToken> rto = refreshTokenRepository.findByTokenHash(hashed);
        if (rto.isEmpty()) {
            return null;
        }

        RefreshToken stored = rto.get();
        if (stored.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(stored);
            return null;
        }

        refreshTokenRepository.delete(stored);
        String newRefresh = generateSecureToken(64);
        RefreshToken rt = RefreshToken.builder()
                .tokenHash(sha256(newRefresh))
                .user(stored.getUser())
                .expiryDate(Instant.now().plusMillis(refreshMs))
                .userAgent(request.getHeader("User-Agent"))
                .ipAddress(request.getRemoteAddr())
                .build();
        refreshTokenRepository.save(rt);

        UserDetails ud = org.springframework.security.core.userdetails.User
                .withUsername(stored.getUser().getUsername())
                .password(stored.getUser().getPassword())
                .authorities(stored.getUser().getRoles().stream()
                        .map(r -> "ROLE_" + r.getRole().name())
                        .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                        .toList())
                .build();

        String access = jwtUtils.generateAccessToken(ud);
        return new RefreshResult(access, newRefresh, ud);
    }

    public void logout(HttpServletRequest request) {
        var cookie = getCookie(request, "refresh_token");
        if (cookie == null) return;
        String hashed = sha256(cookie.getValue());
        refreshTokenRepository.findByTokenHash(hashed).ifPresent(refreshTokenRepository::delete);
    }

    public ResponseCookie createAccessCookie(String token) {
        return ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(900)
                .sameSite(sameSite)
                .build();
    }

    public ResponseCookie createRefreshCookie(String token) {
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(refreshMs / 1000)
                .sameSite(sameSite)
                .build();
    }

    public ResponseCookie clearAccessCookie() {
        return ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite(sameSite)
                .build();
    }

    public ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/api/auth/refresh")
                .maxAge(0)
                .sameSite(sameSite)
                .build();
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String generateSecureToken(int byteLen) {
        byte[] bytes = new byte[byteLen];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private Cookie getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies()) {
            if (name.equals(c.getName())) return c;
        }
        return null;
    }

    public record LoginResult(String accessToken, String refreshToken, UserDetails userDetails) {}
    public record RefreshResult(String accessToken, String refreshToken, UserDetails userDetails) {}
}
