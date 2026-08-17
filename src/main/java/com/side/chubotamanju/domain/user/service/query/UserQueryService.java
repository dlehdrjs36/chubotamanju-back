package com.side.chubotamanju.domain.user.service.query;

import com.side.chubotamanju.domain.user.entity.SocialUser;
import com.side.chubotamanju.domain.user.repository.SocialUserRepository;
import com.side.chubotamanju.global.config.security.DiscordUser;
import com.side.chubotamanju.global.config.security.ProviderUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final SocialUserRepository socialUserRepository;

    //회원 조회
    public Optional<SocialUser> findByProviderAndProviderId(String provider, String providerId) {
        return socialUserRepository.findByProviderAndProviderId(provider, providerId);
    }

}
