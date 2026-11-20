package com.online.shop.service;

import com.online.shop.dto.request.LoginRequest;
import com.online.shop.dto.request.OAuth2TokenRequest;
import com.online.shop.dto.request.RegisterRequest;
import com.online.shop.dto.response.AuthResponse;
import com.online.shop.dto.response.UserResponse;
import com.online.shop.security.CustomUserDetails;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);

    UserResponse getCurrentUser(CustomUserDetails userPrincipal);

    AuthResponse processOAuth2Token(OAuth2TokenRequest request);
}
