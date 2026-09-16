package com.side.chubotamanju.domain.user.service.dto;


import com.side.chubotamanju.domain.user.enums.BountySubmissionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BountyMissionSubmissionQueryDto {
    private Long submissionId;
    private Long missionId;
    private String missionTitle;
    private String requesterDiscordUserId;
    private String rewardText;
    private String proofImageUrl;
    private String proofText;
    private BountySubmissionStatus status;
    private String reviewNote;
    private LocalDateTime createdAt;

}
