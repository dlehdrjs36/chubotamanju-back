package com.side.chubotamanju.domain.bounty.controller.dto.api;


import com.side.chubotamanju.domain.user.enums.BountyRank;
import com.side.chubotamanju.domain.user.enums.TeamClearRule;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionServiceDto;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BountyMissionViewDto {
    private Long id;
    private String title;
    private String description;
    private BountyRank missionRank;
    private String rewardText;
    private String requesterDiscordUserId;
    private int rewardLimit;
    private int rewardRemaining;
    private String teamAUserIds;
    private String teamBUserIds;
    private String targetUserIds;
    private BigDecimal minRating;
    private TeamClearRule teamClearRule;
    private Integer teamClearThreshold;

    public static BountyMissionViewDto convert(BountyMissionServiceDto bountyMissionServiceDto) {
        BountyMissionViewDto bountyMissionResponse = new BountyMissionViewDto();
        bountyMissionResponse.setId(bountyMissionServiceDto.getId());
        bountyMissionResponse.setTitle(bountyMissionServiceDto.getTitle());
        bountyMissionResponse.setDescription(bountyMissionServiceDto.getDescription());
        bountyMissionResponse.setMissionRank(bountyMissionServiceDto.getMissionRank());
        bountyMissionResponse.setRewardText(bountyMissionServiceDto.getRewardText());
        bountyMissionResponse.setRequesterDiscordUserId(bountyMissionServiceDto.getRequesterDiscordUserId());
        bountyMissionResponse.setRewardLimit(bountyMissionServiceDto.getRewardLimit());
        bountyMissionResponse.setRewardRemaining(bountyMissionServiceDto.getRewardRemaining());
        bountyMissionResponse.setTeamAUserIds(bountyMissionServiceDto.getTeamAMemberIds());
        bountyMissionResponse.setTeamBUserIds(bountyMissionServiceDto.getTeamBMemberIds());
        bountyMissionResponse.setTargetUserIds(bountyMissionServiceDto.getTargetMemberIds());
        bountyMissionResponse.setMinRating(bountyMissionServiceDto.getMinRating());
        bountyMissionResponse.setTeamClearRule(bountyMissionServiceDto.getTeamClearRule());
        bountyMissionResponse.setTeamClearThreshold(bountyMissionServiceDto.getTeamClearThreshold());

        return bountyMissionResponse;
    }
}
