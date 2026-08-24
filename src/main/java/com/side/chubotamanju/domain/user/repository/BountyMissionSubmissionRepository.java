package com.side.chubotamanju.domain.user.repository;


import com.side.chubotamanju.domain.user.entity.BountyMissionSubmission;
import com.side.chubotamanju.domain.user.enums.BountySubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BountyMissionSubmissionRepository extends JpaRepository<BountyMissionSubmission, Long> {
    Optional<BountyMissionSubmission> findByIdAndGuildId(Long id, String guildId);
    boolean existsByMissionIdAndHunterDiscordUserIdAndStatus(Long missionId, String hunterDiscordUserId, BountySubmissionStatus status);
    List<BountyMissionSubmission> findTop30ByGuildIdAndStatusOrderByCreatedAtDesc(String guildId, BountySubmissionStatus status);
    List<BountyMissionSubmission> findByMissionIdAndStatus(Long missionId, BountySubmissionStatus status);
    List<BountyMissionSubmission> findTop30ByGuildIdAndHunterDiscordUserIdOrderByCreatedAtDesc(String guildId, String hunterDiscordUserId);
    List<BountyMissionSubmission> findByMissionId(Long missionId);
    List<BountyMissionSubmission> findByMissionIdAndHunterDiscordUserIdOrderByCreatedAtDesc(Long missionId, String hunterDiscordUserId);

}
