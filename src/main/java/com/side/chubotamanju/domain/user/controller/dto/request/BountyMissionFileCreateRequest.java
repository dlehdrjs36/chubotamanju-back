package com.side.chubotamanju.domain.user.controller.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BountyMissionFileCreateRequest {
    private String originFileName;
    private String fileUrl;
}
