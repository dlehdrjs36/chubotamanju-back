package com.side.chubotamanju.domain.user.repository;

import com.side.chubotamanju.domain.user.entity.SocialUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialUserRepository extends JpaRepository<SocialUser, Long> {

    Optional<SocialUser> findByProviderAndProviderId(String provider, String providerId);

    boolean existsByProviderAndProviderId(String provider, String providerId);

}
