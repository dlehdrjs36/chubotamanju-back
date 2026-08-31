package com.side.chubotamanju.global.event.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionServiceDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class MissionSubmissionRegisterEvent extends NotificationEvent {
    private final BountyMissionServiceDto bountyMissionServiceDto;

    @Override
    public List<String> getReceiverDiscordId() {
        return List.of(bountyMissionServiceDto.getRequesterDiscordUserId());
    }

    @Override
    public String makeMessage() {
        return "등록하신 의뢰에 보고가 들어왔습니다.";
    }
}
