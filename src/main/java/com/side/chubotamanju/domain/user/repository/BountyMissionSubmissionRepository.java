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
    @Query("select s " +
            "from BountyMissionSubmission s " +
            "join fetch s.mission " +
            "where s.id = :id " +
            "and s.guildId = :guildId " +
            "and s.status = :status")
    Optional<BountyMissionSubmission> findByIdAndGuildIdAndStatus(@Param("id") Long id, @Param("guildId") String guildId, @Param("status") BountySubmissionStatus status);
    boolean existsByMissionIdAndHunterDiscordUserIdAndStatusIn(Long missionId, String hunterDiscordUserId, List<BountySubmissionStatus> status);

    //기존 디스코드 봇 listPendingSubmissions에서 아래의 쿼리 사용 -> findBountyPendingSubmissionDtoView로 대체
    // List<BountyMissionSubmission> findTop30ByGuildIdAndStatusOrderByCreatedAtDesc(String guildId, BountySubmissionStatus status);

    List<BountyMissionSubmission> findByMissionIdAndStatus(Long missionId, BountySubmissionStatus status);
    List<BountyMissionSubmission> findTop30ByGuildIdAndHunterDiscordUserIdOrderByCreatedAtDesc(String guildId, String hunterDiscordUserId);
    List<BountyMissionSubmission> findByMissionId(Long missionId);
    List<BountyMissionSubmission> findByMissionIdAndHunterDiscordUserIdOrderByCreatedAtDesc(Long missionId, String hunterDiscordUserId);
    List<BountyMissionSubmission> findByHunterDiscordUserIdIsNotAndMissionIdAndStatus(String hunterDiscordUserId, Long missionId, BountySubmissionStatus status);

    @Modifying
    @Query("delete from BountyMissionSubmission b where b.id = :id and b.hunterDiscordUserId = :hunterDiscordUserId ")
    int deleteBountyMissionSubmissionInfo(@Param("id") Long id, @Param("hunterDiscordUserId") String hunterDiscordUserId);
}
