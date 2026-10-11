package com.side.chubotamanju.domain.user.controller.dto.response;

import com.side.chubotamanju.domain.user.entity.SocialUser;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SocialUserResponse {

    private Long socialUsersId;
    private String provider;
    private String providerId;
    private String providerUsername;
    private String providerGlobalName;
    private String providerEmail;
    private Boolean providerVerified;

    public static SocialUserResponse from(SocialUser socialUser) {
        return SocialUserResponse.builder()
                .socialUsersId(socialUser.getSocialUsersId())
                .provider(socialUser.getProvider())
                .providerId(socialUser.getProviderId())
                .providerUsername(socialUser.getProviderUsername())
                .providerGlobalName(socialUser.getProviderGlobalName())
                .providerEmail(socialUser.getProviderEmail())
                .providerVerified(socialUser.getProviderVerified())
                .build();
    }

}
