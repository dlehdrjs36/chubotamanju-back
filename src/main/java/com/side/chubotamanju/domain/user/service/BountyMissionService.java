package com.side.chubotamanju.domain.user.service;

import com.side.chubotamanju.domain.user.entity.BountyMission;
import com.side.chubotamanju.domain.user.entity.BountyMissionSubmission;
import com.side.chubotamanju.domain.user.repository.BountyMissionRepository;
import com.side.chubotamanju.domain.user.repository.dto.BountyMissionSearchCondition;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionServiceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BountyMissionService {

    private final BountyMissionRepository bountyMissionRepository;

    /**
     * 길드에서 특정 유저가 등록한 미션 목록 조회
     * @param guildId
     * @return
     */
    public List<BountyMissionServiceDto> findBountyMissionListByUserId(String userId, String guildId, Long lastBountyMissionId, LocalDateTime lastCreatedAt, Pageable pageable) {
        BountyMissionSearchCondition bountyMissionSearchCondition = BountyMissionSearchCondition.builder()
                .guildId(guildId)
                .requesterDiscordUserId(userId)
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
    public void createBountyMission(BountyMissionServiceDto bountyMissionServiceDto) {

        BountyMission bountyMission = BountyMission.create(
                bountyMissionServiceDto.getGuildId()
                , bountyMissionServiceDto.getTitle()
                , bountyMissionServiceDto.getRequesterDiscordUserId()
                , bountyMissionServiceDto.getRewardText()
                , bountyMissionServiceDto.getRewardLimit()
                , bountyMissionServiceDto.getDescription());

        bountyMissionRepository.save(bountyMission);
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
     * 유저 본인이 등록한 미션 취소
     */
    @Transactional
    public void cancleMission(Long id, String userId) {
        BountyMission bountyMission = bountyMissionRepository.findByIdAndRequesterDiscordUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 의뢰 보고를 찾을 수 없어."));

        bountyMission.cancle();
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

}
