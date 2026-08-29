package com.side.chubotamanju.domain.user.service.dto;

import com.side.chubotamanju.domain.user.enums.BountySubmissionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class BountyMissionSubmissionServiceDto {
    private Long submissionId;
    private Long missionId;
    private String hunterDiscordUserId;
    private String proofText;
    private BountySubmissionStatus status;
    private LocalDateTime createdAt;
    private List<BountyMissionFileServiceDto> bountyMissionFileServiceDtoList;
}
