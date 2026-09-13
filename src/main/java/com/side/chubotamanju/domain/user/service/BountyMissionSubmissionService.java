package com.side.chubotamanju.domain.user.service;

import com.side.chubotamanju.domain.bounty.controller.MissionController;
import com.side.chubotamanju.domain.bounty.controller.dto.api.BountyHunterSubmissionDto;
import com.side.chubotamanju.domain.bounty.controller.dto.api.BountyPendingSubmissionDto;
import com.side.chubotamanju.domain.r2.service.R2StorageService;
import com.side.chubotamanju.domain.user.entity.BountyMission;
import com.side.chubotamanju.domain.user.entity.BountyMissionSubmission;
import com.side.chubotamanju.domain.user.enums.BountySubmissionStatus;
import com.side.chubotamanju.domain.user.repository.BountyMissionRepository;
import com.side.chubotamanju.domain.user.repository.BountyMissionSubmissionRepository;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionFileServiceDto;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionSubmissionServiceDto;
import com.side.chubotamanju.domain.user.service.validator.BountyMissionSubmissionValidator;
import com.side.chubotamanju.global.common.CursorUtil;
import com.side.chubotamanju.global.common.PagingRequest;
import com.side.chubotamanju.global.common.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BountyMissionSubmissionService {
    private final BountyMissionRepository bountyMissionRepository;
    private final BountyMissionSubmissionRepository bountyMissionSubmissionRepository;
    private final BountyMissionSubmissionValidator bountyMissionSubmissionValidator;
    private final R2StorageService r2StorageService;

    /**
     * 길드의 특정 미션에 대하여 미션 보고 등록
     * @param discordUserGuildIdList
     * @param hunterDiscordUserId
     * @param missionId
     * @param bountyMissionFileServiceDtoList
     * @param proofText
     */
    @Transactional
    public BountyMissionSubmission submitMissionProof(List<String> discordUserGuildIdList, String hunterDiscordUserId, Long missionId, List<BountyMissionFileServiceDto> bountyMissionFileServiceDtoList, String proofText) {

        //의뢰 보고자의 길드가 해당 미션이 등록된 길드에 속하는지 확인
        BountyMission mission = bountyMissionRepository.findByIdAndGuildIdIn(missionId, discordUserGuildIdList)
                .orElseThrow(() -> new IllegalArgumentException("해당 의뢰를 찾을 수 없어."));

        // BountyMissionSubmission 생성 검증
        bountyMissionSubmissionValidator.validate(mission, hunterDiscordUserId, bountyMissionFileServiceDtoList, proofText);

        // BountyMissionSubmission 생성
        BountyMissionSubmission bountyMissionSubmission = BountyMissionSubmission.create(mission.getGuildId(), mission, hunterDiscordUserId, proofText);

        return bountyMissionSubmissionRepository.save(bountyMissionSubmission);
    }

    /**
     * 길드에 접수된 보고 조회
     * @param submissionId
     * @param guildId
     * @return
     */
    public BountyMissionSubmission findByBountyMissionSubmissionBy(Long submissionId, String guildId, BountySubmissionStatus bountySubmissionStatus) {
        return bountyMissionSubmissionRepository.findByIdAndGuildIdAndStatus(submissionId, guildId, bountySubmissionStatus)
                .orElseThrow(() -> new IllegalArgumentException("길드에서 해당 의뢰 보고를 찾을 수 없거나 이미 검토가 완료되었습니다."));
    }

    /**
     * 미션에 제출된 보고 목록 조회
     * @param missionId
     * @return
     */
    public List<BountyMissionSubmission> findBountyMissionSubmissionByMissionId(Long missionId) {
        return bountyMissionSubmissionRepository.findByMissionId(missionId);
    }

    /**
     * 길드에서 의뢰 수행자가 미션에 제출한 보고 목록 조회(내부용도)
     * @param guildId
     * @param hunterDiscordUserId
     * @param pagingRequest
     * @return
     */
    @Transactional(readOnly = true)
    public PagingResponse<BountyMissionSubmissionServiceDto> findBountyMissionSubmissionList(String guildId, String hunterDiscordUserId, PagingRequest pagingRequest) {
        PagingResponse<BountyMissionSubmission> bountyMissionSubmissionList = bountyMissionSubmissionRepository.findBountyMissionSubmissionList(guildId, hunterDiscordUserId, pagingRequest);
        List<BountyMissionSubmissionServiceDto> bountyMissionServiceDtoList = bountyMissionSubmissionList.getData().stream()
                .map(BountyMissionSubmissionServiceDto::from).
                toList();

        // 첫 페이지 또는 무한 스크롤(Cursor) 조회 요청이면서 결과 데이터가 존재하는 경우에만 커서 생성
        if ((pagingRequest.getPage() == 1 || !pagingRequest.isOffsetMode()) && !bountyMissionSubmissionList.getData().isEmpty()) {
            // 현재 목록의 맨 마지막 아이템을 꺼내서 다음 이정표(커서) 생성
            List<BountyMissionSubmission> currentData = bountyMissionSubmissionList.getData();
            BountyMissionSubmission lastItem = currentData.get(currentData.size() - 1);

            MissionController.MissionsCursor nextCursor = new MissionController.MissionsCursor(
                    lastItem.getId(),
                    lastItem.getCreatedAt()
            );
            // 무한스크롤 전용 커서 조건 설정
            bountyMissionSubmissionList.setCursor(CursorUtil.encodeCursor(nextCursor));
        }

        return bountyMissionSubmissionList.changeData(bountyMissionServiceDtoList);
    }

    /**
     * 길드에서 의뢰 수행자가 미션에 제출한 보고 목록 조회(봇)
     * - 무한스크롤 X, 오프셋 방식으로 동작
     * @param guildId
     * @param hunterDiscordUserId
     * @param pagingRequest
     * @return
     */
    @Transactional(readOnly = true)
    public PagingResponse<BountyHunterSubmissionDto> findBountyMissionSubmissionListView(String guildId, String hunterDiscordUserId, PagingRequest pagingRequest) {
        return bountyMissionSubmissionRepository.findBountyMissionSubmissionListView(guildId, hunterDiscordUserId, pagingRequest);
    }

    /**
     * 현재 미션 수행 완료한 헌터를 제외한 미션에 제출된 대기 상태의 보고 목록 조회
     * @param missionId
     * @param bountySubmissionStatus
     * @return
     */
    public List<BountyMissionSubmission> findBountyMissionSubmissionBy(String approvedHunterDiscordUserId, Long missionId, BountySubmissionStatus bountySubmissionStatus) {
        return bountyMissionSubmissionRepository.findByHunterDiscordUserIdIsNotAndMissionIdAndStatus(approvedHunterDiscordUserId, missionId, bountySubmissionStatus);
    }

    /**
     * 유저가 미션에 제출한 보고 삭제
     * @param submissionId
     * @param userId
     */
    @Transactional
    public int cancelSubmission(Long submissionId, String userId) {
        return bountyMissionSubmissionRepository.deleteBountyMissionSubmissionInfo(submissionId, userId);
    }

    /***
     *
     * @param guildId
     * @param hunterDiscordUserId
     * @param pagingRequest
     * @return
     */
    public PagingResponse<BountyPendingSubmissionDto> findBountyPendingSubmissionDtoView(String guildId, String hunterDiscordUserId, PagingRequest pagingRequest) {

        PagingResponse<BountyPendingSubmissionDto> bountyPendingSubmissionDtoPagingResponse = bountyMissionSubmissionRepository.findBountyPendingSubmissionDtoView(guildId, hunterDiscordUserId, pagingRequest);

        // 첫 페이지 또는 무한 스크롤(Cursor) 조회 요청이면서 결과 데이터가 존재하는 경우에만 커서 생성
        if ((pagingRequest.getPage() == 1 || !pagingRequest.isOffsetMode()) && !bountyPendingSubmissionDtoPagingResponse.getData().isEmpty()) {
            // 현재 목록의 맨 마지막 아이템을 꺼내서 다음 이정표(커서) 생성
            List<BountyPendingSubmissionDto> currentData = bountyPendingSubmissionDtoPagingResponse.getData();
            BountyPendingSubmissionDto lastItem = currentData.get(currentData.size() - 1);

            MissionController.MissionsCursor nextCursor = new MissionController.MissionsCursor(
                    lastItem.getSubmissionId(),
                    lastItem.getSubmittedAt()
            );
            // 무한스크롤 전용 커서 조건 설정
            bountyPendingSubmissionDtoPagingResponse.setCursor(CursorUtil.encodeCursor(nextCursor));
        }

        bountyPendingSubmissionDtoPagingResponse.getData().forEach(bountyPendingSubmissionDto -> bountyPendingSubmissionDto.setProofImageUrl(r2StorageService.generateObjectAccessUrl(bountyPendingSubmissionDto.getProofImageUrl())));


        return bountyPendingSubmissionDtoPagingResponse;
    }
}
