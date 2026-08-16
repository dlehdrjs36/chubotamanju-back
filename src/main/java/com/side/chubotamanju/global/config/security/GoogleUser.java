package com.side.chubotamanju.global.config.security;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Id, Username은 제공자별로 차이존재
 */
public class GoogleUser extends OAuth2ProviderUser{

    public GoogleUser(OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super(oAuth2User.getAttributes(), oAuth2User, clientRegistration);
    }

    @Override
    public String getId() {
        return (String) getAttributes().get("sub");
    }

    @Override
    public String getUsername() {
        return (String) getAttributes().get("sub");
    }
}
