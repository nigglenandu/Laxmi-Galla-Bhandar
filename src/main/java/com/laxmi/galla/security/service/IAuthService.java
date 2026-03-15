package com.laxmi.galla.security.service;

import com.laxmi.galla.security.dto.request.LoginRequest;
import com.laxmi.galla.security.dto.request.SignupRequest;
import com.laxmi.galla.security.dto.response.AuthResponse;
import com.laxmi.galla.core.dto.response.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.Map;

public interface IAuthService {

    ApiResult<Map<String, String>> signup(SignupRequest signupRequest);

    ApiResult<AuthResponse> login(LoginRequest loginRequest,
                                  HttpServletRequest request,
                                  HttpServletResponse response);

    ApiResult<Void> logout(HttpServletRequest request,
                           HttpServletResponse response, Authentication auth);
}