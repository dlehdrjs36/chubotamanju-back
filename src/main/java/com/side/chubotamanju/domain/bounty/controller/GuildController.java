package com.side.chubotamanju.domain.bounty.controller;

import com.side.chubotamanju.domain.user.controller.dto.response.UserGuildResponse;
import com.side.chubotamanju.domain.user.entity.SocialUser;
import com.side.chubotamanju.domain.user.exception.UserNotFoundException;
import com.side.chubotamanju.domain.user.service.DiscordLookupService;
import com.side.chubotamanju.domain.user.service.UserService;
import com.side.chubotamanju.domain.user.service.query.SocialUserQueryService;
import com.side.chubotamanju.global.common.ApiResult;
import com.side.chubotamanju.global.config.security.CustomOAuth2User;
import com.side.chubotamanju.global.exception.enums.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class GuildController {

    private final SocialUserQueryService socialUserQueryService;
    private final UserService userService;
    private final DiscordLookupService discordLookupService;

    @GetMapping("/guilds")
    public ResponseEntity<ApiResult<List<UserGuildResponse>>> getGuildsByUserId(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        SocialUser socialUser = socialUserQueryService.findByProviderAndProviderId(customOAuth2User.getName(), customOAuth2User.getProviderId())
                .orElseThrow(() -> new UserNotFoundException(GlobalErrorCode.USER_NOT_FOUND));

        List<UserGuildResponse> userGuildResponseList = userService.findByDiscordUserId(socialUser.getProviderId())
                .stream()
                .map(userServiceDto -> UserGuildResponse.convert(userServiceDto))
                .toList();

        userGuildResponseList.forEach(userGuildResponse -> {
            Map<String, Object> discordGuild = discordLookupService.getDiscordGuild(userGuildResponse.getGuildId());
            userGuildResponse.setGuildName((String) discordGuild.getOrDefault("name", ""));
            if(discordGuild.get("icon") != null) {
                userGuildResponse.setGuildIcon("https://cdn.discordapp.com/icons/" + userGuildResponse.getGuildId() + "/" + userGuildResponse.getGuildIcon() + ".png");
            }
        });

        return ResponseEntity.ok(ApiResult.success(userGuildResponseList));
    }

}
