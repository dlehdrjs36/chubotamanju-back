package com.side.chubotamanju.domain.bounty.controller.dto.api;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BountyMissionUpdateRequest {
    private String requesterDiscordUserId;
    private String title;
    private String description;
    private String rewardText;
    private BigDecimal minRating;
    private String teamClearRuleSpec;
    private String targetUserIds;
}
