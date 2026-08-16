package com.side.chubotamanju.global.config.security;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class DiscordUser extends OAuth2ProviderUser {

    public DiscordUser(OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super(oAuth2User.getAttributes(), oAuth2User, clientRegistration);
    }

    @Override
    public String getId() {
        return (String) getAttributes().get("id");
    }

    @Override
    public String getUsername() {
        return (String) getAttributes().get("username");
    }

    public String getGlobalName() {
        return (String) getAttributes().get("global_name");
    }

    public Boolean isVerified() {
        return (Boolean) getAttributes().get("verified");
    }
}
