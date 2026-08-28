package com.side.chubotamanju.domain.user.service.dto;

import com.side.chubotamanju.domain.user.controller.dto.request.BountyMissionFileCreateRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class BountyMissionSubmissionServiceDto {
    private Long missionId;
    private String proofText;
    private List<BountyMissionFileServiceDto> bountyMissionFileServiceDtoList;
}
