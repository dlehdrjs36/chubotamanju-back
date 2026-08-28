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

    @GetMapping("/guilds/{guildId}/missions")
    public ResponseEntity<ApiResult<List<BountyMissionResponse>>> findGuildsMissions(
            @PathVariable(name = "guildId") String guildId
            , @RequestParam(name = "lastBountyMissionId", required = false) Long lastBountyMissionId
            , @RequestParam(name = "lastCreatedAt", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedAt
            , @PageableDefault(size = 20) Pageable pageable
    ) {

        List<BountyMissionResponse> bountyMissionResponseList = bountyMissionService.findBountyMissionList(guildId, lastBountyMissionId, lastCreatedAt, pageable)
                .stream()
                .map(bountyMissionServiceDto -> BountyMissionResponse.convert(bountyMissionServiceDto))
                .toList();

        bountyMissionResponseList.forEach(bountyMissionResponse -> {
            Map<String, Object> discordUser = discordLookupService.getDiscordUser(bountyMissionResponse.getRequesterDiscordUserId());
            bountyMissionResponse.setRequesterDiscordUserId((String) discordUser.getOrDefault("global_name", discordUser.getOrDefault("username", "")));
        });

        return ResponseEntity.ok(ApiResult.success(bountyMissionResponseList));
    }

    @GetMapping("/guilds/{guildId}/missions/me")
    public ResponseEntity<ApiResult<List<BountyMissionResponse>>> findMyGuildMissions(
            @PathVariable(name = "guildId") String guildId
            , @RequestParam(name = "lastBountyMissionId", required = false) Long lastBountyMissionId
            , @RequestParam(name = "lastCreatedAt", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedAt
            , @PageableDefault(size = 20) Pageable pageable
            , @AuthenticationPrincipal CustomOAuth2User customOAuth2User
    ) {

        List<BountyMissionResponse> bountyMissionResponseList = bountyMissionService.findBountyMissionListByUserId(customOAuth2User.getProviderId(), guildId, lastBountyMissionId, lastCreatedAt, pageable)
                .stream()
                .map(bountyMissionServiceDto -> BountyMissionResponse.convert(bountyMissionServiceDto))
                .toList();

        bountyMissionResponseList.forEach(bountyMissionResponse -> {
            Map<String, Object> discordUser = discordLookupService.getDiscordUser(bountyMissionResponse.getRequesterDiscordUserId());
            bountyMissionResponse.setRequesterDiscordUserId((String) discordUser.getOrDefault("global_name", discordUser.getOrDefault("username", "")));
        });

        return ResponseEntity.ok(ApiResult.success(bountyMissionResponseList));
    }

    @PostMapping("/missions")
    public ResponseEntity<ApiResult<Void>> saveBountyMission(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User
            , @RequestBody BountyMissionCreateRequest bountyMissionCreateRequest) {

        //파라미터 복합 필드 검증
        UserValidator.validate(bountyMissionCreateRequest);

        BountyMissionServiceDto bountyMissionServiceDto = BountyMissionServiceDto.builder()
                .guildId(bountyMissionCreateRequest.getGuildId())
                .title(bountyMissionCreateRequest.getTitle())
                .rewardText(bountyMissionCreateRequest.getRewardText())
                .rewardLimit(bountyMissionCreateRequest.getRewardLimit())
                .description(bountyMissionCreateRequest.getDescription())
                .requesterDiscordUserId(customOAuth2User.getProviderId())
                .build();

        bountyMissionService.createBountyMission(bountyMissionServiceDto);
        return ResponseEntity.ok(ApiResult.success(null));
    }

    @PostMapping("/missions/proof")
    public ResponseEntity<ApiResult<Void>> proofMission(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User
            , @RequestBody BountyMissionSubmissionCreateRequest bountyMissionSubmissionCreateRequest
    ) {

        List<BountyMissionFileServiceDto> bountyMissionFileServiceDtoList = bountyMissionSubmissionCreateRequest.getBountyMissionFileCreateRequestList()
                .stream()
                .map(bountyMissionFileCreateRequest -> BountyMissionFileServiceDto.builder()
                        .originFileName(bountyMissionFileCreateRequest.getOriginFileName())
                        .fileUrl(bountyMissionFileCreateRequest.getFileUrl())
                        .build())
                .toList();


        //미션 보고 등록 처리
        BountyMissionSubmissionServiceDto bountyMissionSubmissionServiceDto = BountyMissionSubmissionServiceDto.builder()
                .missionId(bountyMissionSubmissionCreateRequest.getMissionId())
                .proofText(bountyMissionSubmissionCreateRequest.getProofText())
                .bountyMissionFileServiceDtoList(bountyMissionFileServiceDtoList)
                .build();

        bountyMissionSubmissionServiceFacade.submitMission(customOAuth2User.getProviderId(), bountyMissionSubmissionServiceDto);

        return ResponseEntity.ok(ApiResult.success(null));
    }


}
