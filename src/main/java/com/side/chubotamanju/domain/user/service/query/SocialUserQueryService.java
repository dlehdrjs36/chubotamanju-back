package com.side.chubotamanju.domain.user.service.query;

import com.side.chubotamanju.domain.user.entity.SocialUser;
import com.side.chubotamanju.domain.user.repository.SocialUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialUserQueryService {

    private final SocialUserRepository socialUserRepository;

    //회원 조회
    public Optional<SocialUser> findByProviderAndProviderId(String provider, String providerId) {
        return socialUserRepository.findByProviderAndProviderId(provider, providerId);
    }

}
