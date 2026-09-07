package com.side.chubotamanju.global.event.dto;

import com.side.chubotamanju.domain.user.service.dto.BountyMissionSubmissionServiceDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MissionSubmissionApproveReviewEvent extends NotificationEvent {
    private BountyMissionSubmissionServiceDto bountyMissionSubmissionServiceDto;

    public MissionSubmissionApproveReviewEvent(BountyMissionSubmissionServiceDto bountyMissionSubmissionServiceDto) {
        super(List.of(bountyMissionSubmissionServiceDto.getHunterDiscordUserId()));
        this.bountyMissionSubmissionServiceDto = bountyMissionSubmissionServiceDto;
    }
}
