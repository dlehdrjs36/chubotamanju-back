package com.side.chubotamanju.domain.user.controller.dto.response;


import com.side.chubotamanju.domain.user.enums.BountySubmissionStatus;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionSubmissionQueryDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BountyMissionSubmissionResponse {
    private Long submissionId;
    private Long missionId;
    private String missionTitle;
    private String requesterDiscordUserId;
    private String rewardText;
    private String proofImageUrl;
    private String proofText;
    private BountySubmissionStatus status;
    private String reviewNote;
    private LocalDateTime createAt;


    public static BountyMissionSubmissionResponse from(BountyMissionSubmissionQueryDto fromDto) {
        return BountyMissionSubmissionResponse.builder()
                .submissionId(fromDto.getSubmissionId())
                .missionId(fromDto.getMissionId())
                .missionTitle(fromDto.getMissionTitle())
                .requesterDiscordUserId(fromDto.getRequesterDiscordUserId())
                .rewardText(fromDto.getRewardText())
                .proofText(fromDto.getProofText())
                .status(fromDto.getStatus())
                .reviewNote(fromDto.getReviewNote())
                .createAt(fromDto.getCreatedAt())
                .build();
    }
}
