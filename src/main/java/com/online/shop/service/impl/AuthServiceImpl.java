package com.online.shop.service.impl;

import com.online.shop.dto.request.LoginRequest;
import com.online.shop.dto.request.OAuth2TokenRequest;
import com.online.shop.dto.request.RegisterRequest;
import com.online.shop.dto.response.AuthResponse;
import com.online.shop.dto.response.UserResponse;
import com.online.shop.entity.AuthProvider;
import com.online.shop.entity.Role;
import com.online.shop.entity.User;
import com.online.shop.exception.BadRequestException;
import com.online.shop.exception.ResourceNotFoundException;
import com.online.shop.repository.UserRepository;
import com.online.shop.security.CustomUserDetails;
import com.online.shop.security.JwtTokenProvider;
import com.online.shop.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        return AuthResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationMs() / 1000)
                .user(UserResponse.from(user))
                .build();
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã được đăng ký trong hệ thống!");
        }

        if (request.getUsername() != null && !request.getUsername().isBlank()
                && userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username đã tồn tại!");
        }

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .role(Role.ROLE_USER)
                .provider(AuthProvider.LOCAL)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        String jwt = tokenProvider.generateTokenFromEmail(savedUser.getEmail(), savedUser.getId());

        return AuthResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationMs() / 1000)
                .user(UserResponse.from(savedUser))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(CustomUserDetails userPrincipal) {
        if (userPrincipal == null) {
            throw new BadRequestException("Người dùng chưa được xác thực");
        }

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public AuthResponse processOAuth2Token(OAuth2TokenRequest request) {
        AuthProvider provider;
        try {
            provider = AuthProvider.valueOf(request.getProvider().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Nhà cung cấp OAuth2 không được hỗ trợ: " + request.getProvider());
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BadRequestException("Email là bắt buộc đối với đăng nhập mạng xã hội");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .map(existingUser -> {
                    if (request.getName() != null && !request.getName().isBlank()) {
                        existingUser.setFullName(request.getName());
                    }
                    if (request.getAvatarUrl() != null && !request.getAvatarUrl().isBlank()) {
                        existingUser.setAvatarUrl(request.getAvatarUrl());
                    }
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(request.getEmail())
                            .fullName(request.getName() != null ? request.getName() : request.getEmail())
                            .avatarUrl(request.getAvatarUrl())
                            .provider(provider)
                            .role(Role.ROLE_USER)
                            .active(true)
                            .build();
                    return userRepository.save(newUser);
                });

        String jwt = tokenProvider.generateTokenFromEmail(user.getEmail(), user.getId());

        return AuthResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationMs() / 1000)
                .user(UserResponse.from(user))
                .build();
    }
}
