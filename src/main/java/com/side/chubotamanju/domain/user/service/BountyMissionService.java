package com.side.chubotamanju.domain.user.service;

import com.side.chubotamanju.domain.bounty.controller.MissionController;
import com.side.chubotamanju.domain.bounty.controller.dto.api.BountyMissionUpdateRequest;
import com.side.chubotamanju.domain.bounty.controller.dto.api.BountyMissionViewDto;
import com.side.chubotamanju.domain.user.entity.BountyMission;
import com.side.chubotamanju.domain.user.enums.BountyMissionStatus;
import com.side.chubotamanju.domain.user.repository.BountyMissionRepository;
import com.side.chubotamanju.domain.user.repository.dto.BountyMissionSearchCondition;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionServiceDto;
import com.side.chubotamanju.global.common.CursorUtil;
import com.side.chubotamanju.global.common.PagingRequest;
import com.side.chubotamanju.global.common.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BountyMissionService {

    private final BountyMissionRepository bountyMissionRepository;

    /**
     * 길드에서 특정 유저가 등록한 미션 목록 조회(웹)
     * @param guildId
     * @return
     */
    public PagingResponse<BountyMissionServiceDto> findBountyMissionListByUserId(String userId, String guildId, PagingRequest pagingRequest) {
        // 요청 검색 필터 생성
        BountyMissionSearchCondition.BountyMissionSearchConditionBuilder bountyMissionSearchConditionBuilder = BountyMissionSearchCondition.builder()
                .guildId(guildId)
                .requesterDiscordUserId(userId);

        if(!pagingRequest.isOffsetMode()) {
            MissionController.MissionsCursor cursor = CursorUtil.getCursorAs(pagingRequest.getCursor(), MissionController.MissionsCursor.class);

            bountyMissionSearchConditionBuilder.lastBountyMissionId(cursor.lastBountyMissionId())
                    .lastCreatedAt(cursor.lastCreatedAt());
        }
        BountyMissionSearchCondition bountyMissionSearchCondition = bountyMissionSearchConditionBuilder.build();

        PagingResponse<BountyMission> bountyMissionPagingResponse = bountyMissionRepository.findBountyMissionList(bountyMissionSearchCondition, pagingRequest);
        List<BountyMissionServiceDto> bountyMissionServiceDtoList = bountyMissionPagingResponse.getData().stream()
                .map(bountyMission -> BountyMissionServiceDto
                        .builder()
                        .id(bountyMission.getId())
                        .guildId(bountyMission.getGuildId())
                        .requesterDiscordUserId(bountyMission.getRequesterDiscordUserId())
                        .title(bountyMission.getTitle())
                        .description(bountyMission.getDescription())
                        .missionRank(bountyMission.getMissionRank())
                        .rewardText(bountyMission.getRewardText())
                        .status(bountyMission.getStatus())
                        .rewardLimit(bountyMission.getRewardLimit())
                        .rewardRemaining(bountyMission.getRewardRemaining())
                        .createdAt(bountyMission.getCreatedAt())
                        .updatedAt(bountyMission.getUpdatedAt())
                        .build()

                )
                .toList();

        // 첫 페이지 또는 무한 스크롤(Cursor) 조회 요청이면서 결과 데이터가 존재하는 경우에만 커서 생성
        if ((pagingRequest.getPage() == 1 || !pagingRequest.isOffsetMode()) && !bountyMissionPagingResponse.getData().isEmpty()) {
            // 현재 목록의 맨 마지막 아이템을 꺼내서 다음 이정표(커서) 생성
            List<BountyMission> currentData = bountyMissionPagingResponse.getData();
            BountyMission lastItem = currentData.get(currentData.size() - 1);

            MissionController.MissionsCursor nextCursor = new MissionController.MissionsCursor(
                    lastItem.getId(),
                    lastItem.getCreatedAt()
            );
            // 무한스크롤 전용 커서 조건 설정
            bountyMissionPagingResponse.setCursor(CursorUtil.encodeCursor(nextCursor));
        }

        return bountyMissionPagingResponse.changeData(bountyMissionServiceDtoList);
    }

    /**
     * 길드에서 특정 유저가 등록한 미션 목록 조회(봇)
     * @param guildId
     * @return
     */
    public PagingResponse<BountyMissionServiceDto> findBountyMissionListByUserIdBot(String userId, String guildId, PagingRequest pagingRequest) {
        BountyMissionSearchCondition.BountyMissionSearchConditionBuilder bountyMissionSearchConditionBuilder = BountyMissionSearchCondition.builder()
                .guildId(guildId)
                .requesterDiscordUserId(userId);

        if(!pagingRequest.isOffsetMode()) {
            MissionController.MissionsCursor cursor = CursorUtil.getCursorAs(pagingRequest.getCursor(), MissionController.MissionsCursor.class);

            bountyMissionSearchConditionBuilder.lastBountyMissionId(cursor.lastBountyMissionId())
                    .lastCreatedAt(cursor.lastCreatedAt());
        }
        BountyMissionSearchCondition bountyMissionSearchCondition = bountyMissionSearchConditionBuilder.build();

        PagingResponse<BountyMission> bountyMissionList = bountyMissionRepository.findBountyMissionList(bountyMissionSearchCondition, pagingRequest);
        List<BountyMissionServiceDto> bountyMissionServiceDtoList = bountyMissionList.getData().stream()
                .map(bountyMission -> BountyMissionServiceDto
                        .builder()
                        .id(bountyMission.getId())
                        .guildId(bountyMission.getGuildId())
                        .requesterDiscordUserId(bountyMission.getRequesterDiscordUserId())
                        .title(bountyMission.getTitle())
                        .description(bountyMission.getDescription())
                        .missionRank(bountyMission.getMissionRank())
                        .rewardText(bountyMission.getRewardText())
                        .status(bountyMission.getStatus())
                        .rewardLimit(bountyMission.getRewardLimit())
                        .rewardRemaining(bountyMission.getRewardRemaining())
                        .createdAt(bountyMission.getCreatedAt())
                        .updatedAt(bountyMission.getUpdatedAt())
                        .build()
                ).toList();

        // 첫 페이지 또는 무한 스크롤(Cursor) 조회 요청이면서 결과 데이터가 존재하는 경우에만 커서 생성
        if ((pagingRequest.getPage() == 1 || !pagingRequest.isOffsetMode()) && !bountyMissionList.getData().isEmpty()) {
            // 현재 목록의 맨 마지막 아이템을 꺼내서 다음 이정표(커서) 생성
            List<BountyMission> currentData = bountyMissionList.getData();
            BountyMission lastItem = currentData.get(currentData.size() - 1);

            MissionController.MissionsCursor nextCursor = new MissionController.MissionsCursor(
                    lastItem.getId(),
                    lastItem.getCreatedAt()
            );
            // 무한스크롤 전용 커서 조건 설정
            bountyMissionList.setCursor(CursorUtil.encodeCursor(nextCursor));
        }

        return bountyMissionList.changeData(bountyMissionServiceDtoList);
    }

    /**
     * 길드 미션 목록 조회(커스텀 페이징 객체)
     * @param guildId
     * @return
     */
    public PagingResponse<BountyMissionServiceDto> findBountyMissionList(String guildId, PagingRequest pagingRequest) {
        // 요청 검색 필터 생성
        BountyMissionSearchCondition.BountyMissionSearchConditionBuilder bountyMissionSearchConditionBuilder = BountyMissionSearchCondition.builder()
                .guildId(guildId);
        if(!pagingRequest.isOffsetMode()) {
            MissionController.MissionsCursor cursor = CursorUtil.getCursorAs(pagingRequest.getCursor(), MissionController.MissionsCursor.class);

            bountyMissionSearchConditionBuilder.lastBountyMissionId(cursor.lastBountyMissionId())
                    .lastCreatedAt(cursor.lastCreatedAt());
        }
        BountyMissionSearchCondition bountyMissionSearchCondition = bountyMissionSearchConditionBuilder.build();

        //프로젝션으로 변경필요
        PagingResponse<BountyMission> bountyMissionList = bountyMissionRepository.findBountyMissionList(bountyMissionSearchCondition, pagingRequest);
        List<BountyMissionServiceDto> bountyMissionServiceDtoList = bountyMissionList.getData().stream()
                .map(bountyMission -> BountyMissionServiceDto
                    .builder()
                    .id(bountyMission.getId())
                    .guildId(bountyMission.getGuildId())
                    .requesterDiscordUserId(bountyMission.getRequesterDiscordUserId())
                    .title(bountyMission.getTitle())
                    .description(bountyMission.getDescription())
                    .missionRank(bountyMission.getMissionRank())
                    .rewardText(bountyMission.getRewardText())
                    .status(bountyMission.getStatus())
                    .rewardLimit(bountyMission.getRewardLimit())
                    .rewardRemaining(bountyMission.getRewardRemaining())
                    .createdAt(bountyMission.getCreatedAt())
                    .updatedAt(bountyMission.getUpdatedAt())
                    .build()
                ).toList();

        // 첫 페이지 또는 무한 스크롤(Cursor) 조회 요청이면서 결과 데이터가 존재하는 경우에만 커서 생성
        if ((pagingRequest.getPage() == 1 || !pagingRequest.isOffsetMode()) && !bountyMissionList.getData().isEmpty()) {
            // 현재 목록의 맨 마지막 아이템을 꺼내서 다음 이정표(커서) 생성
            List<BountyMission> currentData = bountyMissionList.getData();
            BountyMission lastItem = currentData.get(currentData.size() - 1);

            MissionController.MissionsCursor nextCursor = new MissionController.MissionsCursor(
                    lastItem.getId(),
                    lastItem.getCreatedAt()
            );
            // 무한스크롤 전용 커서 조건 설정
            bountyMissionList.setCursor(CursorUtil.encodeCursor(nextCursor));
        }

        return bountyMissionList.changeData(bountyMissionServiceDtoList);
    }

    /**
     * 길드 미션 목록 조회(메인)
     * @param guildId
     * @return
     */
    public List<BountyMissionServiceDto> findBountyMissionList(String guildId, Long lastBountyMissionId, LocalDateTime lastCreatedAt, Pageable pageable) {
        BountyMissionSearchCondition bountyMissionSearchCondition = BountyMissionSearchCondition.builder()
                .guildId(guildId)
                .lastBountyMissionId(lastBountyMissionId)
                .lastCreatedAt(lastCreatedAt)
                .build();

        return bountyMissionRepository.findBountyMissionList(bountyMissionSearchCondition, pageable)
                .stream()
                .map(bountyMission -> BountyMissionServiceDto
                        .builder()
                        .id(bountyMission.getId())
                        .guildId(bountyMission.getGuildId())
                        .requesterDiscordUserId(bountyMission.getRequesterDiscordUserId())
                        .title(bountyMission.getTitle())
                        .description(bountyMission.getDescription())
                        .missionRank(bountyMission.getMissionRank())
                        .rewardText(bountyMission.getRewardText())
                        .status(bountyMission.getStatus())
                        .rewardLimit(bountyMission.getRewardLimit())
                        .rewardRemaining(bountyMission.getRewardRemaining())
                        .createdAt(bountyMission.getCreatedAt())
                        .updatedAt(bountyMission.getUpdatedAt())
                        .build()

                )
                .toList();
    }


    /**
     * 미션 등록
     * @param bountyMissionServiceDto
     */
    @Transactional
    public Long createBountyMission(BountyMissionServiceDto bountyMissionServiceDto) {

        BountyMission bountyMission = BountyMission.create(
                bountyMissionServiceDto.getGuildId()
                , bountyMissionServiceDto.getTitle()
                , bountyMissionServiceDto.getRequesterDiscordUserId()
                , bountyMissionServiceDto.getRewardText()
                , bountyMissionServiceDto.getRewardLimit()
                , bountyMissionServiceDto.getDescription());

        BountyMission save = bountyMissionRepository.save(bountyMission);

        return save.getId();
    }

    /**
     * 미션에 등록된 길드 조회
     */
    public BountyMissionServiceDto findByMissionId(Long missionId) {
        BountyMission bountyMission = bountyMissionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 의뢰를 찾을 수 없어."));

        return BountyMissionServiceDto.from(bountyMission);
    }

    /**
     * 유저 본인이 등록한 미션 삭제
     */
    @Transactional
    public int cancelMission(Long missionId, String requesterDiscordUserId) {
        return bountyMissionRepository.deleteBountyMissionInfo(missionId, requesterDiscordUserId);
    }

    /**
     * 유저 본인이 등록한 미션 수정
     */
    @Transactional
    public BountyMissionViewDto updateMission(Long missionId, String requesterDiscordUserId, BountyMissionUpdateRequest bountyMissionUpdateRequest) {
        BountyMission bountyMission = bountyMissionRepository.findByIdAndRequesterDiscordUserIdAndStatus(missionId, requesterDiscordUserId, BountyMissionStatus.OPEN)
                .orElseThrow(() -> new IllegalArgumentException("해당 의뢰를 찾을 수 없어."));

        bountyMission.updateByBotRequest(bountyMissionUpdateRequest);

        return BountyMissionViewDto.from(BountyMissionServiceDto.from(bountyMission));

    }

    /**
     * 유저가 등록한 미션인지 검증
     * @param missionId
     * @param requestUserId
     * @return
     */
    public BountyMission findByIdAndRequesterDiscordUserId(Long missionId, String requestUserId) {
        return bountyMissionRepository.findByIdAndRequesterDiscordUserId(missionId, requestUserId)
                .orElseThrow(() -> new IllegalArgumentException("해당 의뢰를 찾을 수 없어."));
    }

    /**
     * 유저가 등록한 완료되지 않은 미션 단건 조회
     * @param missionId
     * @param requestUserId
     * @return
     */
    public BountyMissionViewDto findBountyMissionViewDtoInfo(Long missionId, String guildId, String requestUserId) {
        BountyMission bountyMission = bountyMissionRepository.findByIdAndGuildIdAndRequesterDiscordUserIdAndStatus(missionId, guildId, requestUserId, BountyMissionStatus.OPEN)
                .orElseThrow(() -> new IllegalArgumentException("해당 의뢰를 찾을 수 없어."));

        return BountyMissionViewDto.from(BountyMissionServiceDto.from(bountyMission));
    }

}
