package com.side.chubotamanju.domain.bounty.controller.dto.api;

import com.side.chubotamanju.domain.user.entity.BountyMissionFile;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionFileServiceDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

//TODO 추후 컨트롤러 응답 타입 개선 필요
@Getter
@Setter
@Builder
public class BountyMissionFileResponse {
    private Long submissionId;
    private String originFileName;
    private String fileUrl;

    public static BountyMissionFileResponse from(BountyMissionFileServiceDto bountyMissionFile) {
        return BountyMissionFileResponse.builder()
                .submissionId(bountyMissionFile.getSubmissionId())
                .originFileName(bountyMissionFile.getOriginFileName())
                .fileUrl(bountyMissionFile.getFileUrl())
                .build();
    }
}
