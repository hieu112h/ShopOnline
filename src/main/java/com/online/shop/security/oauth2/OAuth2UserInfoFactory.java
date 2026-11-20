package com.online.shop.security.oauth2;

import com.online.shop.entity.AuthProvider;
import com.online.shop.exception.BadRequestException;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.GITHUB.toString())) {
            return new GithubOAuth2UserInfo(attributes);
        } else {
            throw new BadRequestException("Đăng nhập với " + registrationId + " chưa được hỗ trợ.");
        }
    }
}
