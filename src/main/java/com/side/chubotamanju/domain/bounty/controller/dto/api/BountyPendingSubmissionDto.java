package com.side.chubotamanju.domain.bounty.controller.dto.api;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 접수된 의뢰보고 듣기 api
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BountyPendingSubmissionDto {
    private Long submissionId;
    private Long missionId;
    private String missionTitle;
    private String rewardText;
    private String hunterDiscordUserId;
    private String proofImageUrl;
    private String proofText;
    private LocalDateTime submittedAt;
}
