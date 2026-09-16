package com.side.chubotamanju.domain.bounty.controller.dto.api;

import com.side.chubotamanju.domain.user.service.dto.BountyMissionFileServiceDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<BountyMissionFileServiceDto> bountyMissionFileList;
//    private String proofImageUrl;
//    private String proofText;
    private LocalDateTime submittedAt;
}
