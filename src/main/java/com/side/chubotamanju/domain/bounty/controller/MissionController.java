package com.side.chubotamanju.domain.bounty.controller;

import com.side.chubotamanju.domain.bounty.controller.validator.MissionValidator;
import com.side.chubotamanju.domain.user.controller.dto.request.BountyMissionCreateRequest;
import com.side.chubotamanju.domain.user.controller.dto.request.BountyMissionSubmissionCreateRequest;
import com.side.chubotamanju.domain.user.controller.dto.request.BountyMissionSubmissionReviewCreateRequest;
import com.side.chubotamanju.domain.user.controller.dto.response.BountyMissionResponse;
import com.side.chubotamanju.domain.user.controller.validator.UserValidator;
import com.side.chubotamanju.domain.user.service.*;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionFileServiceDto;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionServiceDto;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionSubmissionServiceDto;
import com.side.chubotamanju.global.common.ApiResult;
import com.side.chubotamanju.global.common.PagingRequest;
import com.side.chubotamanju.global.common.PagingResponse;
import com.side.chubotamanju.global.config.security.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class MissionController {

    private final DiscordLookupService discordLookupService;
    private final BountyMissionService bountyMissionService;
    private final BountyMissionSubmissionServiceFacade bountyMissionSubmissionServiceFacade;
    private final BountyMissionAttemptService bountyMissionAttemptService;


    public record MissionsCursor(Long lastBountyMissionId, LocalDateTime lastCreatedAt) {}

    /**
     * 특정 길드에 등록된 미션 목록 조회
     * @param guildId
     * @param lastBountyMissionId
     * @param lastCreatedAt
     * @param pageable
     * @return
     */
    @GetMapping("/guilds/{guildId}/missions")
//    public ResponseEntity<ApiResult<List<BountyMissionResponse>>> findGuildsMissions(
    public ResponseEntity<ApiResult<PagingResponse<BountyMissionResponse, MissionsCursor>>> findGuildsMissions(
            @PathVariable(name = "guildId") String guildId
            , PagingRequest<MissionsCursor> pagingRequest
//            , @RequestParam(name = "lastBountyMissionId", required = false) Long lastBountyMissionId
//            , @RequestParam(name = "lastCreatedAt", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedAt
//            , @PageableDefault(size = 20) Pageable pageable
    ) {

//        List<BountyMissionResponse> bountyMissionResponseList = bountyMissionService.findBountyMissionList(guildId, lastBountyMissionId, lastCreatedAt, pageable)
//                .stream()
//                .map(bountyMissionServiceDto -> BountyMissionResponse.convert(bountyMissionServiceDto))
//                .toList();

        PagingResponse<BountyMissionServiceDto, MissionsCursor> bountyMissionList = bountyMissionService.findBountyMissionList(guildId, pagingRequest);
        List<BountyMissionResponse> bountyMissionResponseList = bountyMissionList.getData()
                .stream()
                .map(bountyMissionServiceDto -> BountyMissionResponse.convert(bountyMissionServiceDto))
                .toList();

        bountyMissionResponseList.forEach(bountyMissionResponse -> {
            Map<String, Object> discordUser = discordLookupService.getDiscordUser(bountyMissionResponse.getRequesterDiscordUserId());
            bountyMissionResponse.setRequesterDiscordUserId((String) discordUser.getOrDefault("global_name", discordUser.getOrDefault("username", "")));
        });

        return ResponseEntity.ok(ApiResult.success(new PagingResponse<>(bountyMissionResponseList, bountyMissionList.isHasNext())));
    }

    /**
     * 특정 길드에 유저 본인이 등록한 미션 목록 조회
     * @param guildId
     * @param lastBountyMissionId
     * @param lastCreatedAt
     * @param pageable
     * @param customOAuth2User
     * @return
     */
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

    /**
     * 길드에 미션 등록
     * @param customOAuth2User
     * @param bountyMissionCreateRequest
     * @return
     */
    @PostMapping("/missions")
    public ResponseEntity<ApiResult<Long>> saveBountyMission(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User
            , @RequestBody BountyMissionCreateRequest bountyMissionCreateRequest) {

        //파라미터 복합 필드 검증
        MissionValidator.validate(bountyMissionCreateRequest);

        BountyMissionServiceDto bountyMissionServiceDto = BountyMissionServiceDto.builder()
                .guildId(bountyMissionCreateRequest.getGuildId())
                .title(bountyMissionCreateRequest.getTitle())
                .rewardText(bountyMissionCreateRequest.getRewardText())
                .rewardLimit(bountyMissionCreateRequest.getRewardLimit())
                .description(bountyMissionCreateRequest.getDescription())
                .requesterDiscordUserId(customOAuth2User.getProviderId())
                .build();

        return ResponseEntity.ok(ApiResult.success(bountyMissionService.createBountyMission(bountyMissionServiceDto)));
    }

    /**
     * 길드에 등록된 미션 보고
     * @param customOAuth2User
     * @param bountyMissionSubmissionCreateRequest
     * @return
     */
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

    /**
     * 유저 본인이 등록한 미션 취소
     * @param missionId
     * @param customOAuth2User
     * @return
     */
    @PostMapping("/missions/{missionId}/cancle")
    public ResponseEntity<ApiResult<Void>> cancelMission(
            @PathVariable Long missionId
            , @AuthenticationPrincipal CustomOAuth2User customOAuth2User
    ) {

        bountyMissionService.cancleMission(missionId, customOAuth2User.getProviderId());

        return ResponseEntity.ok(ApiResult.success(null));
    }

    /**
     * 미션에 보고한 헌터 목록 조회
     * @param missionId
     * @param customOAuth2User
     * @return
     */
    @GetMapping("/missions/{missionId}/submissions")
    public ResponseEntity<ApiResult<List<BountyMissionSubmissionServiceDto>>> findMissionSubmissionsByMissionId(
            @PathVariable Long missionId
            , @AuthenticationPrincipal CustomOAuth2User customOAuth2User
    ) {

        //요청한 사람이 미션의 의뢰자인지 검증
        List<BountyMissionSubmissionServiceDto> bountyMissionSubmissionServiceDtoList = bountyMissionSubmissionServiceFacade.findBountyMissionSubmissionByRequestUserIdAndMissionId(customOAuth2User.getProviderId(), missionId);

        bountyMissionSubmissionServiceDtoList.forEach(bountyMissionSubmissionServiceDto -> {
            Map<String, Object> discordUser = discordLookupService.getDiscordUser(bountyMissionSubmissionServiceDto.getHunterDiscordUserId());
            bountyMissionSubmissionServiceDto.setHunterDiscordUserId((String) discordUser.getOrDefault("global_name", discordUser.getOrDefault("username", "")));
        });

        return ResponseEntity.ok(ApiResult.success(bountyMissionSubmissionServiceDtoList));
    }

    /**
     * 미션 검토
     * @param submissionId
     * @param bountyMissionSubmissionReviewCreateRequest
     * @param customOAuth2User
     * @return
     */
    @PostMapping("/submissions/{submissionId}/review")
    public ResponseEntity<ApiResult<Void>> reviewSubmission(
            @PathVariable Long submissionId
            , @RequestBody BountyMissionSubmissionReviewCreateRequest bountyMissionSubmissionReviewCreateRequest
            , @AuthenticationPrincipal CustomOAuth2User customOAuth2User
    ) {

        bountyMissionSubmissionServiceFacade.reviewSubmission(bountyMissionSubmissionReviewCreateRequest.getGuildId()
                , customOAuth2User.getProviderId()
                , submissionId
                , bountyMissionSubmissionReviewCreateRequest.getNote()
                , bountyMissionSubmissionReviewCreateRequest.isApprove()
        );

        return ResponseEntity.ok(ApiResult.success(null));
    }

    /**
     * 헌터의 미션 도전 기록 목록 조회
     * 검색 옵션
     * @return
     */
    @GetMapping("/attempts")
    public ResponseEntity<ApiResult<List<Void>>> findMissionAttemptsBy(
            @PathVariable(name = "guildId") String guildId
            , @RequestParam(name = "lastBountyMissionId", required = false) Long lastBountyMissionId
            , @RequestParam(name = "lastCreatedAt", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedAt
            , @PageableDefault(size = 20) Pageable pageable
            , @AuthenticationPrincipal CustomOAuth2User customOAuth2User
    ) {

        //요청한 사람이 미션의 의뢰자인지 검증
        bountyMissionAttemptService.searchBountyMissionAttemptsBy(guildId, customOAuth2User.getProviderId(), lastBountyMissionId, lastCreatedAt, pageable);

//        bountyMissionSubmissionServiceDtoList.forEach(bountyMissionSubmissionServiceDto -> {
//            Map<String, Object> discordUser = discordLookupService.getDiscordUser(bountyMissionSubmissionServiceDto.getHunterDiscordUserId());
//            bountyMissionSubmissionServiceDto.setHunterDiscordUserId((String) discordUser.getOrDefault("global_name", discordUser.getOrDefault("username", "")));
//        });

        return ResponseEntity.ok(ApiResult.success(null));
    }
}
