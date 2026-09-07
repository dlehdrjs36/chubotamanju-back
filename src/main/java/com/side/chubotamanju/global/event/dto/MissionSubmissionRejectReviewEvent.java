package com.side.chubotamanju.global.event.dto;

import com.side.chubotamanju.domain.user.service.dto.BountyMissionSubmissionServiceDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MissionSubmissionRejectReviewEvent extends NotificationEvent {
    private List<BountyMissionSubmissionServiceDto> bountyMissionSubmissionServiceDtoList;

    public MissionSubmissionRejectReviewEvent(List<BountyMissionSubmissionServiceDto> bountyMissionSubmissionServiceDtoList) {
        super(bountyMissionSubmissionServiceDtoList
                .stream()
                .map(BountyMissionSubmissionServiceDto::getHunterDiscordUserId)
                .toList()
        );
        this.bountyMissionSubmissionServiceDtoList = bountyMissionSubmissionServiceDtoList;
    }
}
