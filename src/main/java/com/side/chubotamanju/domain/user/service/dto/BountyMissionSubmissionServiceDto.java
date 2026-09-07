package com.side.chubotamanju.domain.user.service.dto;

import com.side.chubotamanju.domain.user.entity.BountyMissionSubmission;
import com.side.chubotamanju.domain.user.enums.BountySubmissionStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BountyMissionSubmissionServiceDto {
    private Long submissionId;
    private Long missionId;
    private String hunterDiscordUserId;
    private String proofText;
    private BountySubmissionStatus status;
    private LocalDateTime createdAt;
    private List<BountyMissionFileServiceDto> bountyMissionFileServiceDtoList;

    public static BountyMissionSubmissionServiceDto from(BountyMissionSubmission bountyMissionSubmission) {
        return BountyMissionSubmissionServiceDto.builder()
                .submissionId(bountyMissionSubmission.getId())
                .missionId(bountyMissionSubmission.getMissionId())
                .hunterDiscordUserId(bountyMissionSubmission.getHunterDiscordUserId())
                .proofText(bountyMissionSubmission.getProofText())
                .status(bountyMissionSubmission.getStatus())
                .createdAt(bountyMissionSubmission.getCreatedAt())
                .build();
    }
}
