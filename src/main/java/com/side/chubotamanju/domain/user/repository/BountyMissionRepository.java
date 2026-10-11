package com.side.chubotamanju.domain.user.repository;


import com.side.chubotamanju.domain.user.entity.BountyMission;
import com.side.chubotamanju.domain.user.entity.BountyMissionSubmission;
import com.side.chubotamanju.domain.user.enums.BountyMissionStatus;
import com.side.chubotamanju.domain.user.enums.BountyRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BountyMissionRepository extends JpaRepository<BountyMission, Long>, BountyMissionRepositoryCustom {
    List<BountyMission> findByGuildIdAndPromotionOnlyFalseAndStatusOrderByIdDesc(String guildId, BountyMissionStatus status);

    List<BountyMission> findByGuildIdAndPromotionOnlyFalseAndStatusAndMissionRankOrderByIdDesc(String guildId, BountyMissionStatus status, BountyRank missionRank);

    List<BountyMission> findByGuildIdAndRequesterDiscordUserIdAndPromotionOnlyFalseAndStatusOrderByIdDesc(
            String guildId,
            String requesterDiscordUserId,
            BountyMissionStatus status
    );

    @Query("""
            SELECT m FROM BountyMission m
            WHERE m.promotionOnly = true
              AND m.status = :status
              AND m.missionRank = :missionRank
              AND (m.guildId = :guildId OR m.guildId = 'GLOBAL')
            """)
    List<BountyMission> findPromotionMissions(String guildId, BountyMissionStatus status, BountyRank missionRank);

    Optional<BountyMission> findByIdAndGuildId(Long id, String guildId);

    //디스코드 유저가 속한 길드의 미션을 조회
    Optional<BountyMission> findByIdAndGuildIdIn(Long id, List<String> guildIdList);

    //디스코드 유저가 등록한 미션 조회
    Optional<BountyMission> findByIdAndRequesterDiscordUserId(Long id, String requesterDiscordUserId);

    //디스코드 유저가 등록한 미션 조회
    Optional<BountyMission> findByIdAndGuildIdAndRequesterDiscordUserIdAndStatus(Long id, String guildId, String requesterDiscordUserId, BountyMissionStatus status);
    Optional<BountyMission> findByIdAndGuildIdAndRequesterDiscordUserId(Long id, String guildId, String requesterDiscordUserId);

    //디스코드 유저가 등록한 미션 조회
    Optional<BountyMission> findByIdAndRequesterDiscordUserIdAndStatus(Long id, String requesterDiscordUserId, BountyMissionStatus status);

    //유저가 등록한 미션 삭제
    @Modifying
    @Query("delete from BountyMission b where b.id = :id and b.requesterDiscordUserId = :requesterDiscordUserId")
    int deleteBountyMissionInfo(@Param("id") Long id, @Param("requesterDiscordUserId") String requesterDiscordUserId);
}
