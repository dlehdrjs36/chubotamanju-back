package com.side.chubotamanju.domain.user.controller.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BountyMissionCreateRequest {
    private String guildId;
    private String guildName;
    private String title;
    private String rewardText;
    private int rewardLimit;
    private String description;

}
