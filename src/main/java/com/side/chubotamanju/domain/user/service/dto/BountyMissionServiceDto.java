package com.side.chubotamanju.domain.user.service.dto;

import com.side.chubotamanju.domain.user.entity.BountyMission;
import com.side.chubotamanju.domain.user.enums.BountyMissionStatus;
import com.side.chubotamanju.domain.user.enums.BountyRank;
import com.side.chubotamanju.domain.user.enums.TeamClearRule;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BountyMissionServiceDto {
    private Long id;
    private String guildId;
    private String requesterDiscordUserId;
    private String title;
    private String description;
    private BountyRank missionRank;
    private String rewardText;
    private boolean promotionOnly;
    private BountyMissionStatus status;
    private String completedByDiscordUserId;
    private int rewardLimit;
    private int rewardRemaining;
    private String teamAMemberIds;
    private String teamBMemberIds;
    private String targetMemberIds;
    private TeamClearRule teamClearRule;
    private Integer teamClearThreshold;
    private BigDecimal minRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BountyMissionServiceDto from(BountyMission bountyMission) {
        return BountyMissionServiceDto.builder()
                .id(bountyMission.getId())
                .guildId(bountyMission.getGuildId())
                .requesterDiscordUserId(bountyMission.getRequesterDiscordUserId())
                .title(bountyMission.getTitle())
                .description(bountyMission.getDescription())
                .missionRank(bountyMission.getMissionRank())
                .rewardText(bountyMission.getRewardText())
                .promotionOnly(bountyMission.isPromotionOnly())
                .status(bountyMission.getStatus())
                .completedByDiscordUserId(bountyMission.getCompletedByDiscordUserId())
                .rewardLimit(bountyMission.getRewardLimit())
                .rewardRemaining(bountyMission.getRewardRemaining())
                .createdAt(bountyMission.getCreatedAt())
                .updatedAt(bountyMission.getUpdatedAt())
                .build();
    }
}
