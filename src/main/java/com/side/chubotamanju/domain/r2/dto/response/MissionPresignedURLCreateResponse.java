package com.side.chubotamanju.domain.r2.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MissionPresignedURLCreateResponse {
    private String uploadUrl;
    private String method;
    private String fileKey;
}
