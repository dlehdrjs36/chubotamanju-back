package com.side.chubotamanju.domain.user.controller;

import com.side.chubotamanju.domain.user.controller.dto.request.BountyMissionCreateRequest;
import com.side.chubotamanju.domain.user.controller.dto.request.BountyMissionFileCreateRequest;
import com.side.chubotamanju.domain.user.controller.dto.request.BountyMissionSubmissionCreateRequest;
import com.side.chubotamanju.domain.user.controller.dto.response.BountyMissionResponse;
import com.side.chubotamanju.domain.user.controller.dto.response.UserGuildResponse;
import com.side.chubotamanju.domain.user.controller.validator.UserValidator;
import com.side.chubotamanju.domain.user.entity.BountyMissionSubmission;
import com.side.chubotamanju.domain.user.entity.SocialUser;
import com.side.chubotamanju.domain.user.exception.UserNotFoundException;
import com.side.chubotamanju.domain.user.service.*;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionFileServiceDto;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionServiceDto;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionSubmissionServiceDto;
import com.side.chubotamanju.domain.user.service.query.SocialUserQueryService;
import com.side.chubotamanju.global.common.ApiResult;
import com.side.chubotamanju.global.config.security.CustomOAuth2User;
import com.side.chubotamanju.global.exception.enums.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final SocialUserQueryService socialUserQueryService;
    private final UserService userService;
    private final BountyMissionService bountyMissionService;
    private final BountyMissionSubmissionServiceFacade bountyMissionSubmissionServiceFacade;
    private final DiscordLookupService discordLookupService;

    @GetMapping("/me")
    public ResponseEntity<ApiResult<SocialUser>> getMe(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        SocialUser socialUser = socialUserQueryService.findByProviderAndProviderId(customOAuth2User.getName(), customOAuth2User.getProviderId())
                .orElseThrow(() -> new UserNotFoundException(GlobalErrorCode.USER_NOT_FOUND));

        return ResponseEntity.ok(ApiResult.success(socialUser));
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
