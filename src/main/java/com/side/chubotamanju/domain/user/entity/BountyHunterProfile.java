package com.side.chubotamanju.domain.user.entity;

import com.side.chubotamanju.domain.user.enums.BountyRank;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bounty_hunter_profiles")
@Getter
@Setter
public class BountyHunterProfile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guild_id", nullable = false)
    private String guildId;

    @Column(name = "discord_user_id", nullable = false)
    private String discordUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_rank", nullable = false)
    private BountyRank currentRank;

    @Column(name = "contribution", nullable = false)
    private int contribution;

    @Enumerated(EnumType.STRING)
    @Column(name = "pending_promotion_rank")
    private BountyRank pendingPromotionRank;

    @Column(name = "active_promotion_mission_id")
    private Long activePromotionMissionId;


    /**
     * 헌터가 가지고 있던 점수에 미션 랭크에 부여된 점수 추가
     * - 미션 랭크별 배정된 점수를 기존 헌터 점수에 더한다.
     * @param contributionEarned
     */
    public void addContribution(int contributionEarned) {
        this.contribution = this.contribution + contributionEarned;
    }

}
