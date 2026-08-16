package com.side.chubotamanju.domain.user.service;

import com.side.chubotamanju.domain.user.entity.SocialUser;
import com.side.chubotamanju.domain.user.repository.SocialUserRepository;
import com.side.chubotamanju.global.config.security.DiscordUser;
import com.side.chubotamanju.global.config.security.ProviderUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SocialUserRepository socialUserRepository;

    //회원 조회
    public Optional<SocialUser> findByProviderAndProviderId(String provider, String providerId) {
        return socialUserRepository.findByProviderAndProviderId(provider, providerId);
    }

    //회원 가입
    public void register(ProviderUser providerUser) {
        SocialUser.SocialUserBuilder socialUserBuilder = SocialUser.builder()
                .provider(providerUser.getProvider())
                .providerId(providerUser.getId())
                .providerUsername(providerUser.getUsername())
                .providerEmail(providerUser.getEmail());

        //디스코드에 특화된 필드 저장
        if(providerUser.getProvider().equals("discord")) {
            DiscordUser discordUser = (DiscordUser) providerUser;

            socialUserBuilder.providerGlobalName(discordUser.getGlobalName())
                    .providerVerified(discordUser.isVerified());
        }

        SocialUser socialUser = socialUserBuilder.build();
        socialUserRepository.save(socialUser);
    }

}
