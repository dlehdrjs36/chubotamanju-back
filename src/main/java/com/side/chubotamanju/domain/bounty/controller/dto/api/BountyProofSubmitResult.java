package com.side.chubotamanju.domain.bounty.controller.dto.api;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BountyProofSubmitResult {
    private Long submissionId;
    private Long missionId;
    private String proofImageUrl;
    private String proofText;
    private String requesterDiscordUserId;
}
