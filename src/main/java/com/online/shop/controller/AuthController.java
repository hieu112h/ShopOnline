package com.online.shop.controller;

import com.online.shop.dto.common.ApiResponse;
import com.online.shop.dto.request.LoginRequest;
import com.online.shop.dto.request.OAuth2TokenRequest;
import com.online.shop.dto.request.RegisterRequest;
import com.online.shop.dto.response.AuthResponse;
import com.online.shop.dto.response.UserResponse;
import com.online.shop.security.CustomUserDetails;
import com.online.shop.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Đăng nhập thành công"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Đăng ký tài khoản thành công"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userPrincipal) {
        UserResponse response = authService.getCurrentUser(userPrincipal);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy thông tin người dùng thành công"));
    }

    @PostMapping("/oauth2/token")
    public ResponseEntity<ApiResponse<AuthResponse>> processOAuth2Token(@Valid @RequestBody OAuth2TokenRequest request) {
        AuthResponse response = authService.processOAuth2Token(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Xác thực OAuth2 thành công"));
    }
}
