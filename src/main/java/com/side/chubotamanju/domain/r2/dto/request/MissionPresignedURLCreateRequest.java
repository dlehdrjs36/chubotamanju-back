package com.side.chubotamanju.domain.r2.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MissionPresignedURLCreateRequest {
    private String missionId;
    private String fileName;
}
