package com.side.chubotamanju.global.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;

/**
 * 일반적으로 공통으로 갖는 속성
 */
public abstract class OAuth2ProviderUser implements ProviderUser {

    private OAuth2User oAuth2User;
    private ClientRegistration clientRegistration;
    private Map<String, Object> attributes;

    public OAuth2ProviderUser(Map<String, Object> attributes, OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        this.oAuth2User = oAuth2User;
        this.clientRegistration = clientRegistration;
        this.attributes = attributes;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getEmail() {
        return (String) getAttributes().getOrDefault("email", "");
    }

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getProvider() {
        return clientRegistration.getRegistrationId();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
