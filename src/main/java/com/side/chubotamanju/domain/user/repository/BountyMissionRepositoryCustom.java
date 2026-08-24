package com.side.chubotamanju.domain.user.repository;

import com.side.chubotamanju.domain.user.entity.BountyMission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

public interface BountyMissionRepositoryCustom {
    Slice<BountyMission> findBountyMissionList(String guildId, Long lastBountyMissionId, LocalDateTime lastCreatedAt, Pageable pageable);
}
