package com.online.shop.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuth2TokenRequest {

    @NotBlank(message = "Provider không được để trống (ví dụ: google, github)")
    private String provider;

    @NotBlank(message = "Token không được để trống")
    private String token;

    private String email;
    private String name;
    private String avatarUrl;
}
