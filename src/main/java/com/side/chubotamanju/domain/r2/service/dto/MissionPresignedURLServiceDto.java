package com.side.chubotamanju.domain.r2.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MissionPresignedURLServiceDto {
    private String uploadUrl;
    private String method;
}
