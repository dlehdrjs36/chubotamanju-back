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

}
