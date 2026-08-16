package com.side.chubotamanju.domain.user.service;

import com.side.chubotamanju.domain.user.entity.SocialUser;
import com.side.chubotamanju.global.config.security.DiscordUser;
import com.side.chubotamanju.global.config.security.GoogleUser;
import com.side.chubotamanju.global.config.security.ProviderUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AbstractOAuth2UserService {

    private final UserService userService;

    public ProviderUser providerUser(ClientRegistration clientRegistration, OAuth2User oAuth2User) {
        String registrationId = clientRegistration.getRegistrationId();

        if(registrationId.equals("discord")) {
            return new DiscordUser(oAuth2User, clientRegistration);

        } else if(registrationId.equals("google")) {
            return new GoogleUser(oAuth2User, clientRegistration);

        }
        return null;
    }

    // 회원가입
    public void register(ProviderUser providerUser) {
        Optional<SocialUser> optionalSocialUser = userService.findByProviderAndProviderId(providerUser.getProvider(), providerUser.getId());
        if(optionalSocialUser.isEmpty()) {
            userService.register(providerUser);
        }
    }
}
