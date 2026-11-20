package com.online.shop.dto.response;

import com.online.shop.entity.AuthProvider;
import com.online.shop.entity.Role;
import com.online.shop.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String username;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String avatarUrl;
    private Role role;
    private AuthProvider provider;
    private Boolean active;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .provider(user.getProvider())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
