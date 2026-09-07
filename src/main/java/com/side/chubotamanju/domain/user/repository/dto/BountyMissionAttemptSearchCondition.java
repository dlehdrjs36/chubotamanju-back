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
public class BountyMissionAttemptSearchCondition {
    private final String guildId;
    private final String hunterDiscordUserId;
    private final Long lastBountyMissionAttemptId;
    private final LocalDateTime lastCreatedAt;
}
