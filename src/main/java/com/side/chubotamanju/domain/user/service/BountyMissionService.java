package com.side.chubotamanju.domain.user.service;

import com.side.chubotamanju.domain.user.entity.BountyMission;
import com.side.chubotamanju.domain.user.repository.BountyMissionRepository;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionServiceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BountyMissionService {

    private final BountyMissionRepository bountyMissionRepository;

    /**
     * 길드 미션 목록 조회
     * @param guildId
     * @return
     */
    public List<BountyMissionServiceDto> findBountyMissionList(String guildId, Long lastBountyMissionId, LocalDateTime lastCreatedAt, Pageable pageable) {
        return bountyMissionRepository.findBountyMissionList(guildId, lastBountyMissionId, lastCreatedAt, pageable)
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

}
