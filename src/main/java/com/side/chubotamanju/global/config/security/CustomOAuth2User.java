package com.side.chubotamanju.global.config.security;

import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Builder
public class CustomOAuth2User implements OAuth2User {
    private String provider;
    private String providerId;
    private Map<String, Object> attributes;
    private Collection<? extends GrantedAuthority> authorities;

    public String getProviderId() {
        return this.providerId;
    }

    @Override
    public String getName() {
        return this.provider;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }
}
