package com.side.chubotamanju.global.event.dto;

import com.side.chubotamanju.domain.user.service.dto.BountyMissionServiceDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MissionSubmissionRegisterEvent {
    private final BountyMissionServiceDto bountyMissionServiceDto;

}
