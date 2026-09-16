package com.side.chubotamanju.domain.bounty.controller;

import com.side.chubotamanju.domain.bounty.controller.dto.api.*;
import com.side.chubotamanju.domain.bounty.controller.validator.MissionValidator;
import com.side.chubotamanju.domain.user.controller.dto.request.BountyMissionCreateRequest;
import com.side.chubotamanju.domain.user.controller.dto.request.BountyMissionSubmissionCreateRequest;
import com.side.chubotamanju.domain.user.controller.dto.request.BountyMissionSubmissionReviewCreateRequest;
import com.side.chubotamanju.domain.user.controller.dto.response.BountyMissionResponse;
import com.side.chubotamanju.domain.user.controller.dto.response.BountyMissionSubmissionResponse;
import com.side.chubotamanju.domain.user.service.*;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionSubmissionQueryDto;
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
    private final BountyMissionSubmissionService bountyMissionSubmissionService;


    public record MissionsCursor(Long lastBountyMissionId, LocalDateTime lastCreatedAt) {}

    /***
     * 특정 길드에 등록된 미션 목록 조회(웹)
     * @param guildId
     * @param pagingRequest
     * @return
     */
    @GetMapping("/guilds/{guildId}/missions")
    public ResponseEntity<ApiResult<PagingResponse<BountyMissionResponse>>> findGuildsMissions(
            @PathVariable(name = "guildId") String guildId
            , PagingRequest pagingRequest
    ) {

        PagingResponse<BountyMissionServiceDto> bountyMissionList = bountyMissionService.findBountyMissionList(guildId, pagingRequest);
        List<BountyMissionResponse> bountyMissionResponseList = bountyMissionList.getData()
                .stream()
                .map(BountyMissionResponse::convert)
                .toList();

        bountyMissionResponseList.forEach(bountyMissionResponse -> {
            Map<String, Object> discordUser = discordLookupService.getDiscordUser(bountyMissionResponse.getRequesterDiscordUserId());
            bountyMissionResponse.setRequesterDiscordUserId((String) discordUser.getOrDefault("global_name", discordUser.getOrDefault("username", "")));
        });

        PagingResponse<BountyMissionResponse> bountyMissionResponsePagingResponse = bountyMissionList.changeData(bountyMissionResponseList);

        return ResponseEntity.ok(ApiResult.success(bountyMissionResponsePagingResponse));

    }


    /**
     * 특정 길드에 등록된 미션 목록 조회(봇 전용 API)
     * @param guildId
     * @return
     */
    @GetMapping("/guilds/{guildId}/missions/bot")
    public ResponseEntity<ApiResult<PagingResponse<BountyMissionViewDto>>> findGuildsMissionsBot(
            @PathVariable(name = "guildId") String guildId
            , PagingRequest pagingRequest
    ) {

        PagingResponse<BountyMissionServiceDto> bountyMissionList = bountyMissionService.findBountyMissionList(guildId, pagingRequest);
        List<BountyMissionViewDto> bountyMissionResponseList = bountyMissionList.getData()
                .stream()
                .map(bountyMissionServiceDto -> BountyMissionViewDto.from(bountyMissionServiceDto))
                .toList();

//        bountyMissionList.getData().forEach(bountyMissionResponse -> {
//            Map<String, Object> discordUser = discordLookupService.getDiscordUser(bountyMissionResponse.getRequesterDiscordUserId());
//            bountyMissionResponse.setRequesterDiscordUserId((String) discordUser.getOrDefault("global_name", discordUser.getOrDefault("username", "")));
//        });

        PagingResponse<BountyMissionViewDto> bountyMissionViewDtoPagingResponse = bountyMissionList.changeData(bountyMissionResponseList);

        return ResponseEntity.ok(ApiResult.success(bountyMissionViewDtoPagingResponse));

    }

    /**
     * 특정 길드에 유저 본인이 등록한 미션 목록 조회(웹)
     * @param guildId
     * @param customOAuth2User
     * @param pagingRequest
     * @return
     */
    @GetMapping("/guilds/{guildId}/missions/me")
    public ResponseEntity<ApiResult<PagingResponse<BountyMissionResponse>>> findMyGuildMissions(
            @PathVariable(name = "guildId") String guildId
            , @AuthenticationPrincipal CustomOAuth2User customOAuth2User
            , PagingRequest pagingRequest
//            , @RequestParam(name = "lastBountyMissionId", required = false) Long lastBountyMissionId
//            , @RequestParam(name = "lastCreatedAt", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedAt
//            , @PageableDefault(size = 20) Pageable pageable
    ) {

        PagingResponse<BountyMissionServiceDto> bountyMissionServiceDtoPagingResponse = bountyMissionService.findBountyMissionListByUserId(customOAuth2User.getProviderId(), guildId, pagingRequest);
        List<BountyMissionResponse> bountyMissionResponseList = bountyMissionServiceDtoPagingResponse.getData().stream()
                .map(BountyMissionResponse::convert)
                .toList();

        bountyMissionResponseList.forEach(bountyMissionResponse -> {
            Map<String, Object> discordUser = discordLookupService.getDiscordUser(bountyMissionResponse.getRequesterDiscordUserId());
            bountyMissionResponse.setRequesterDiscordUserId((String) discordUser.getOrDefault("global_name", discordUser.getOrDefault("username", "")));
        });

        return ResponseEntity.ok(ApiResult.success(bountyMissionServiceDtoPagingResponse.changeData(bountyMissionResponseList)));
    }

    /**
     * 특정 길드에 유저 본인이 등록한 미션 목록 조회(봇 전용 API)
     * @param guildId
     * @param customOAuth2User
     * @param pagingRequest
     * @return
     */
    @GetMapping("/guilds/{guildId}/missions/me/bot")
    public ResponseEntity<ApiResult<PagingResponse<BountyMissionViewDto>>> findMyGuildMissionsBot(
            @PathVariable(name = "guildId") String guildId
            , @AuthenticationPrincipal CustomOAuth2User customOAuth2User
            , PagingRequest pagingRequest
    ) {

        PagingResponse<BountyMissionServiceDto> bountyMissionList = bountyMissionService.findBountyMissionListByUserIdBot(customOAuth2User.getProviderId(), guildId, pagingRequest);
        List<BountyMissionViewDto> bountyMissionResponseList = bountyMissionList.getData()
                .stream()
                .map(BountyMissionViewDto::from)
                .toList();

        return ResponseEntity.ok(ApiResult.success(bountyMissionList.changeData(bountyMissionResponseList)));
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
     * 길드에 등록된 미션 보고(봇)
     * @param customOAuth2User
     * @param bountyMissionSubmissionCreateRequest
     * @return
     */
    @PostMapping("/bot/missions/proof")
    public ResponseEntity<ApiResult<BountyProofSubmitResult>> proofMissionBot(
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

        BountyProofSubmitResult bountyProofSubmitResult = bountyMissionSubmissionServiceFacade.submitMissionBot(customOAuth2User.getProviderId(), bountyMissionSubmissionServiceDto);

        return ResponseEntity.ok(ApiResult.success(bountyProofSubmitResult));
    }

    /**
     * 유저 본인이 등록한 미션 조회(봇)
     * @param guildId
     * @param missionId
     * @param customOAuth2User
     * @return
     */
    @GetMapping("/guilds/{guildId}/missions/{missionId}/bot")
    public ResponseEntity<ApiResult<BountyMissionViewDto>> searchMissionInfo(
            @PathVariable("guildId") String guildId
            , @PathVariable("missionId") Long missionId
            , @AuthenticationPrincipal CustomOAuth2User customOAuth2User
    ) {

        BountyMissionViewDto bountyMissionViewDtoInfo = bountyMissionService.findBountyMissionViewDtoInfo(missionId, guildId, customOAuth2User.getProviderId());

        return ResponseEntity.ok(ApiResult.success(bountyMissionViewDtoInfo));
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

        bountyMissionService.cancelMission(missionId, customOAuth2User.getProviderId());

        return ResponseEntity.ok(ApiResult.success(null));
    }

    /**
     * 유저 본인이 등록한 미션 수정(봇)
     * @param missionId
     * @param customOAuth2User
     * @return
     */
    @PatchMapping("/missions/{missionId}/bot")
    public ResponseEntity<ApiResult<BountyMissionViewDto>> updateMissionInfo(
            @PathVariable Long missionId
            , @AuthenticationPrincipal CustomOAuth2User customOAuth2User
            , @RequestBody BountyMissionUpdateRequest bountyMissionUpdateRequest
    ) {

        BountyMissionViewDto bountyMissionViewDto = bountyMissionService.updateMission(missionId, customOAuth2User.getProviderId(), bountyMissionUpdateRequest);

        return ResponseEntity.ok(ApiResult.success(bountyMissionViewDto));
    }

    /**
     * 유저가 미션에 제출한 보고 삭제
     * @param submissionId
     * @param customOAuth2User
     * @return
     */
    @PostMapping("/submissions/{submissionId}/cancle")
    public ResponseEntity<ApiResult<Void>> cancelSubmission(
            @PathVariable Long submissionId
            , @AuthenticationPrincipal CustomOAuth2User customOAuth2User
    ) {

        bountyMissionSubmissionService.cancelSubmission(submissionId, customOAuth2User.getProviderId());

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
     * 내가 등록한 미션들에 보고된 제출 목록 조회(봇)
     * @param customOAuth2User
     * @return
     */
    @GetMapping("/bot/guilds/{guildId}/my-missions/submissions")
    public ResponseEntity<ApiResult<PagingResponse<BountyPendingSubmissionDto>>> findMissionSubmissions(
            @PathVariable("guildId") String guildId
            , @AuthenticationPrincipal CustomOAuth2User customOAuth2User
            , PagingRequest pagingRequest
    ) {

        //요청한 사람이 미션의 의뢰자인지 검증
        PagingResponse<BountyPendingSubmissionDto> bountyPendingSubmissionDtoList = bountyMissionSubmissionService.findBountyPendingSubmissionDtoView(guildId, customOAuth2User.getProviderId(), pagingRequest);

        return ResponseEntity.ok(ApiResult.success(bountyPendingSubmissionDtoList));
    }

    /**
     * 미션 검토(웹)
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
     * 미션 검토 - 완료, 거부 (봇)
     * @param submissionId
     * @param bountyMissionSubmissionReviewCreateRequest
     * @param customOAuth2User
     * @return
     */
    @PostMapping("/bot/submissions/{submissionId}/review")
    public ResponseEntity<ApiResult<BountySubmissionReviewResult>> reviewSubmissionBot (
            @PathVariable Long submissionId
            , @RequestBody BountyMissionSubmissionReviewCreateRequest bountyMissionSubmissionReviewCreateRequest
            , @AuthenticationPrincipal CustomOAuth2User customOAuth2User
    ) {

        BountySubmissionReviewResult bountySubmissionReviewResult = bountyMissionSubmissionServiceFacade.reviewSubmissionBot(bountyMissionSubmissionReviewCreateRequest.getGuildId()
                , customOAuth2User.getProviderId()
                , submissionId
                , bountyMissionSubmissionReviewCreateRequest.getNote()
                , bountyMissionSubmissionReviewCreateRequest.isApprove()
        );

        return ResponseEntity.ok(ApiResult.success(bountySubmissionReviewResult));
    }

    /**
     * TODO 진행 예정
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
            , PagingRequest pagingRequest
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

    /**
     * 내 진행 중 의뢰(미션) 목록 조회(웹)
     * - 무한스크롤 페이징
     * @param guildId
     * @param customOAuth2User
     * @return
     */
    @GetMapping("/guilds/{guildId}/my-submissions")
    public ResponseEntity<ApiResult<PagingResponse<BountyMissionSubmissionResponse>>> findMyMissionSubmissions(
            @PathVariable(name = "guildId") String guildId
            , @AuthenticationPrincipal CustomOAuth2User customOAuth2User
            , PagingRequest pagingRequest
    ) {
        PagingResponse<BountyMissionSubmissionQueryDto> bountyMissionSubmissionListView = bountyMissionSubmissionService.findBountyMissionSubmissionListView(guildId, customOAuth2User.getProviderId(), pagingRequest);
        List<BountyMissionSubmissionResponse> bountyMissionSubmissionResponseList = bountyMissionSubmissionListView.getData().stream()
                .map(BountyMissionSubmissionResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResult.success(bountyMissionSubmissionListView.changeData(bountyMissionSubmissionResponseList)));
    }

    /**
     * 내 진행 중 의뢰(미션) 목록 조회(봇)
     * - 오프셋 방식 페이징
     * @param guildId
     * @param customOAuth2User
     * @return
     */
    @GetMapping("/bot/guilds/{guildId}/my-submissions")
    public ResponseEntity<ApiResult<PagingResponse<BountyHunterSubmissionDto>>> findMissionSubmissionsByMissionIdBot(
            @PathVariable(name = "guildId") String guildId
            , @AuthenticationPrincipal CustomOAuth2User customOAuth2User
            , PagingRequest pagingRequest
    ) {
        PagingResponse<BountyMissionSubmissionQueryDto> bountyMissionSubmissionListView = bountyMissionSubmissionService.findBountyMissionSubmissionListView(guildId, customOAuth2User.getProviderId(), pagingRequest);
        List<BountyHunterSubmissionDto> bountyHunterSubmissionDtoList = bountyMissionSubmissionListView.getData().stream()
                .map(BountyHunterSubmissionDto::from)
                .toList();

        return ResponseEntity.ok(ApiResult.success(bountyMissionSubmissionListView.changeData(bountyHunterSubmissionDtoList)));
    }
}
