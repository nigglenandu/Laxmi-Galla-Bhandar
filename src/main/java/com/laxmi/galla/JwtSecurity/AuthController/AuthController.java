package com.laxmi.galla.JwtSecurity.AuthController;

import com.laxmi.galla.JwtSecurity.model.AuthUserEntity;
import com.laxmi.galla.JwtSecurity.model.Role;
import com.laxmi.galla.JwtSecurity.model.RoleEntity;
import com.laxmi.galla.JwtSecurity.payload.JwtResponse;
import com.laxmi.galla.JwtSecurity.payload.LoginRequest;
import com.laxmi.galla.JwtSecurity.payload.MessageResponse;
import com.laxmi.galla.JwtSecurity.payload.SignupRequest;
import com.laxmi.galla.JwtSecurity.repository.RoleRepository;
import com.laxmi.galla.JwtSecurity.repository.UserRepository;
import com.laxmi.galla.JwtSecurity.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public AuthController(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          AuthService authService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        var loginResult = authService.login(loginRequest, request);

        // Set cookies
        response.addHeader("Set-Cookie", authService.createAccessCookie(loginResult.accessToken()).toString());
        response.addHeader("Set-Cookie", authService.createRefreshCookie(loginResult.refreshToken()).toString());

        AuthUserEntity user = userRepository.findByUsername(loginResult.userDetails().getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<String> roles = user.getRoles().stream()
                .map(r -> r.getRole().name())
                .collect(Collectors.toSet());

        return ResponseEntity.ok(new JwtResponse(
                loginResult.accessToken(),
                loginResult.refreshToken(),
                roles.stream().toList(),
                user.getUserId(),
                user.getUsername(),
                user.getEmail()
        ));
    }

    @PostMapping("signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        if(userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        AuthUserEntity user = new AuthUserEntity();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setEmail(signupRequest.getEmail());

        Set<RoleEntity> roles = signupRequest.getRoles().stream()
                .map(roleName -> roleRepository.findByRole(Role.valueOf(roleName))
                        .orElseThrow(() -> new RuntimeException("Error: Role " + roleName + " not found!"))
                ).collect(Collectors.toSet());

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request);

        response.addHeader("Set-Cookie", authService.clearAccessCookie().toString());
        response.addHeader("Set-Cookie", authService.clearRefreshCookie().toString());

        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    @PostMapping("refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        AuthService.RefreshResult refreshResult = authService.refresh(request);

        if (refreshResult == null) {
            return ResponseEntity.status(401)
                    .body(new MessageResponse("Invalid or expired refresh token"));
        }

        ResponseCookie accessCookie = authService.createAccessCookie(refreshResult.accessToken());
        ResponseCookie refreshCookie = authService.createRefreshCookie(refreshResult.refreshToken());
        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        AuthUserEntity user = userRepository.findByUsername(refreshResult.userDetails().getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<String> roles = user.getRoles().stream()
                .map(r -> r.getRole().name())
                .collect(Collectors.toSet());

        return ResponseEntity.ok(new JwtResponse(
                refreshResult.accessToken(),
                refreshResult.refreshToken(),
                roles.stream().toList(),
                user.getUserId(),
                user.getUsername(),
                user.getEmail()
        ));
    }
}
