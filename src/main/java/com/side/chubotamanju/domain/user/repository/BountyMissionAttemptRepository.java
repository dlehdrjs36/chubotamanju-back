package com.side.chubotamanju.domain.user.repository;

import com.side.chubotamanju.domain.user.entity.BountyMissionAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BountyMissionAttemptRepository extends JpaRepository<BountyMissionAttempt, Long> {
    Optional<BountyMissionAttempt> findByMissionIdAndHunterDiscordUserId(Long missionId, String hunterDiscordUserId);
    List<BountyMissionAttempt> findByMissionId(Long missionId);
}
