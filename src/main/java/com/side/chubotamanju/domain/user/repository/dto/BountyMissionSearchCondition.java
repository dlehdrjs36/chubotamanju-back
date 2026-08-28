package com.side.chubotamanju.domain.user.repository.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 미션 목록 검색 조건
 */
@Getter
@Setter
@Builder
public class BountyMissionSearchCondition {
    private final String guildId;
    private final String requesterDiscordUserId;
    private final Long lastBountyMissionId;
    private final LocalDateTime lastCreatedAt;
}
