package com.side.chubotamanju.global.event.dto;

import com.side.chubotamanju.domain.user.service.dto.BountyMissionServiceDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MissionSubmissionRegisterEvent extends NotificationEvent {
    private BountyMissionServiceDto bountyMissionServiceDto;

    public MissionSubmissionRegisterEvent(BountyMissionServiceDto bountyMissionServiceDto) {
        super(List.of(bountyMissionServiceDto.getRequesterDiscordUserId()));
        this.bountyMissionServiceDto = bountyMissionServiceDto;
    }
}
