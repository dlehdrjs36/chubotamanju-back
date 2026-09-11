package com.side.chubotamanju.domain.user.repository;


import com.side.chubotamanju.domain.user.entity.BountyMissionSubmission;
import com.side.chubotamanju.domain.user.enums.BountySubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BountyMissionSubmissionRepository extends JpaRepository<BountyMissionSubmission, Long>, BountyMissionSubmissionRepositoryCustom {
    Optional<BountyMissionSubmission> findByIdAndGuildIdAndStatus(Long id, String guildId, BountySubmissionStatus status);
    boolean existsByMissionIdAndHunterDiscordUserIdAndStatus(Long missionId, String hunterDiscordUserId, BountySubmissionStatus status);
    List<BountyMissionSubmission> findTop30ByGuildIdAndStatusOrderByCreatedAtDesc(String guildId, BountySubmissionStatus status);
    List<BountyMissionSubmission> findByMissionIdAndStatus(Long missionId, BountySubmissionStatus status);
    List<BountyMissionSubmission> findTop30ByGuildIdAndHunterDiscordUserIdOrderByCreatedAtDesc(String guildId, String hunterDiscordUserId);
    List<BountyMissionSubmission> findByMissionId(Long missionId);
    List<BountyMissionSubmission> findByMissionIdAndHunterDiscordUserIdOrderByCreatedAtDesc(Long missionId, String hunterDiscordUserId);
    List<BountyMissionSubmission> findByHunterDiscordUserIdIsNotAndMissionIdAndStatus(String hunterDiscordUserId, Long missionId, BountySubmissionStatus status);

    @Modifying
    @Query("delete from BountyMissionSubmission b where b.id = :id and b.hunterDiscordUserId = :hunterDiscordUserId ")
    int deleteBountyMissionSubmissionInfo(@Param("id") Long id, @Param("hunterDiscordUserId") String hunterDiscordUserId);
}
