package com.side.chubotamanju.domain.bounty.controller.dto.api;


import com.side.chubotamanju.domain.user.enums.BountySubmissionStatus;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionSubmissionQueryDto;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionSubmissionServiceDto;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BountyHunterSubmissionDto {
    private Long submissionId;
    private Long missionId;
    private String missionTitle;
    private String requesterDiscordUserId;
    private String rewardText;
    private String proofImageUrl;
    private String proofText;
    private BountySubmissionStatus status;
    private String reviewNote;

    public static BountyHunterSubmissionDto from(BountyMissionSubmissionServiceDto fromDto) {
        return BountyHunterSubmissionDto.builder()
                .submissionId(fromDto.getSubmissionId())
                .missionId(fromDto.getMissionId())
                .requesterDiscordUserId(fromDto.getHunterDiscordUserId())
                .proofText(fromDto.getProofText())
                .status(fromDto.getStatus())
                .build();
    }

    public static BountyHunterSubmissionDto from(BountyMissionSubmissionQueryDto fromDto) {
        return BountyHunterSubmissionDto.builder()
                .submissionId(fromDto.getSubmissionId())
                .missionId(fromDto.getMissionId())
                .missionTitle(fromDto.getMissionTitle())
                .requesterDiscordUserId(fromDto.getRequesterDiscordUserId())
                .rewardText(fromDto.getRewardText())
                .proofText(fromDto.getProofText())
                .status(fromDto.getStatus())
                .reviewNote(fromDto.getReviewNote())
                .build();
    }
}
