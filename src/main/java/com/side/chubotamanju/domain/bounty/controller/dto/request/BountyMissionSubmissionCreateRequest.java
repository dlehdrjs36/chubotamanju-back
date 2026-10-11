package com.side.chubotamanju.domain.bounty.controller.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BountyMissionSubmissionCreateRequest {
    private Long missionId;
    private String proofText;
    private List<BountyMissionFileCreateRequest> bountyMissionFileCreateRequestList = new ArrayList<>();
}
