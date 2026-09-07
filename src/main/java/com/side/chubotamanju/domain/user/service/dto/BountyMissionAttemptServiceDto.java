package com.side.chubotamanju.domain.user.service.dto;

import com.side.chubotamanju.domain.user.entity.BountyMission;
import com.side.chubotamanju.domain.user.entity.BountyMissionAttempt;
import com.side.chubotamanju.domain.user.enums.BountyMissionStatus;
import com.side.chubotamanju.domain.user.enums.BountyRank;
import com.side.chubotamanju.domain.user.enums.TeamClearRule;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BountyMissionAttemptServiceDto {

    private Long id;
    private String guildId;
    private Long missionId;
    private String hunterDiscordUserId;
    private BountyRank hunterRank;
    private boolean rewardGranted;
    private String rewardText;
    private int contributionEarned;
    private boolean promotionAttempt;
    private LocalDateTime createdAt;

    public static BountyMissionAttemptServiceDto from(BountyMissionAttempt bountyMissionAttempt) {
        return BountyMissionAttemptServiceDto.builder()
                .id(bountyMissionAttempt.getId())
                .guildId(bountyMissionAttempt.getGuildId())
                .missionId(bountyMissionAttempt.getMissionId())
                .hunterDiscordUserId(bountyMissionAttempt.getHunterDiscordUserId())
                .hunterRank(bountyMissionAttempt.getHunterRank())
                .rewardGranted(bountyMissionAttempt.isRewardGranted())
                .rewardText(bountyMissionAttempt.getRewardText())
                .contributionEarned(bountyMissionAttempt.getContributionEarned())
                .promotionAttempt(bountyMissionAttempt.isPromotionAttempt())
                .createdAt(bountyMissionAttempt.getCreatedAt())
                .build();
    }
}
