package com.side.chubotamanju.domain.user.controller;

import com.side.chubotamanju.domain.user.controller.dto.response.SocialUserResponse;
import com.side.chubotamanju.domain.user.entity.SocialUser;
import com.side.chubotamanju.domain.user.exception.UserNotFoundException;
import com.side.chubotamanju.domain.user.service.DiscordLookupService;
import com.side.chubotamanju.domain.user.service.query.SocialUserQueryService;
import com.side.chubotamanju.global.common.ApiResult;
import com.side.chubotamanju.global.config.security.CustomOAuth2User;
import com.side.chubotamanju.global.exception.enums.GlobalErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final SocialUserQueryService socialUserQueryService;

    @GetMapping("/me")
    public ResponseEntity<ApiResult<SocialUserResponse>> getMe(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        SocialUser socialUser = socialUserQueryService.findByProviderAndProviderId(customOAuth2User.getName(), customOAuth2User.getProviderId())
                .orElseThrow(() -> new UserNotFoundException(GlobalErrorCode.USER_NOT_FOUND));

        return ResponseEntity.ok(ApiResult.success(SocialUserResponse.from(socialUser)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResult<Void>> logout(HttpServletResponse response) {
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", "")
                .path("/")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        return ResponseEntity.ok(ApiResult.success(null));
    }

}
