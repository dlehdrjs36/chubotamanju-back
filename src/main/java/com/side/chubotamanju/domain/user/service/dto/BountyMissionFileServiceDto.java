package com.side.chubotamanju.domain.user.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BountyMissionFileServiceDto {
    private Long submissionId;
    private String originFileName;
    private String fileUrl;
}
