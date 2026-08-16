package com.side.chubotamanju.domain.user.controller;

import com.side.chubotamanju.domain.user.entity.SocialUser;
import com.side.chubotamanju.domain.user.service.UserService;
import com.side.chubotamanju.global.common.ApiResult;
import com.side.chubotamanju.global.config.security.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResult<SocialUser> getMe(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Optional<SocialUser> optionalSocialUser = userService.findByProviderAndProviderId(customOAuth2User.getName(), customOAuth2User.getProviderId());
        SocialUser socialUser = optionalSocialUser.orElse(null);

        return ApiResult.success(socialUser);
    }
}
