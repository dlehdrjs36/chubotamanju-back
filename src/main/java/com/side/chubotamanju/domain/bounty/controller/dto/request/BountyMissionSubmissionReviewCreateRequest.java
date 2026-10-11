package com.side.chubotamanju.domain.bounty.controller.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BountyMissionSubmissionReviewCreateRequest {
    private String guildId;
    private String note;
    private boolean approve;
}
