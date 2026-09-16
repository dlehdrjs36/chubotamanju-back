package com.side.chubotamanju.domain.user.service.dto;

import com.side.chubotamanju.domain.user.entity.BountyMissionFile;
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

    public static BountyMissionFileServiceDto from(BountyMissionFile bountyMissionFile) {
        return BountyMissionFileServiceDto.builder()
                .submissionId(bountyMissionFile.getBountyMissionSubmission().getId())
                .originFileName(bountyMissionFile.getOriginFileName())
                .fileUrl(bountyMissionFile.getFileUrl())
                .build();
    }
}
