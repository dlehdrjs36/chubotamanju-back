package com.side.chubotamanju.domain.bounty.controller.dto.response;

import com.side.chubotamanju.domain.user.enums.BountyMissionStatus;
import com.side.chubotamanju.domain.user.enums.BountyRank;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionServiceDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BountyMissionResponse {
    private Long id;
    private String guildId;
    private String requesterDiscordUserId;
    private String title;
    private String description;
    private BountyRank missionRank;
    private String rewardText;
    private BountyMissionStatus status;
    private String completedByDiscordUserId;
    private int rewardLimit;
    private int rewardRemaining;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BountyMissionResponse from(BountyMissionServiceDto bountyMissionServiceDto) {
        BountyMissionResponse bountyMissionResponse = new BountyMissionResponse();
        bountyMissionResponse.setId(bountyMissionServiceDto.getId());
        bountyMissionResponse.setGuildId(bountyMissionServiceDto.getGuildId());
        bountyMissionResponse.setRequesterDiscordUserId(bountyMissionServiceDto.getRequesterDiscordUserId());
        bountyMissionResponse.setTitle(bountyMissionServiceDto.getTitle());
        bountyMissionResponse.setDescription(bountyMissionServiceDto.getDescription());
        bountyMissionResponse.setMissionRank(bountyMissionServiceDto.getMissionRank());
        bountyMissionResponse.setRewardText(bountyMissionServiceDto.getRewardText());
        bountyMissionResponse.setStatus(bountyMissionServiceDto.getStatus());
        bountyMissionResponse.setRewardLimit(bountyMissionServiceDto.getRewardLimit());
        bountyMissionResponse.setRewardRemaining(bountyMissionServiceDto.getRewardRemaining());
        bountyMissionResponse.setCreatedAt(bountyMissionServiceDto.getCreatedAt());
        bountyMissionResponse.setUpdatedAt(bountyMissionServiceDto.getUpdatedAt());
        
        return bountyMissionResponse;
    }

}
