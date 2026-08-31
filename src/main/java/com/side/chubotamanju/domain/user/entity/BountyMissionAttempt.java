package com.side.chubotamanju.domain.user.entity;

import com.side.chubotamanju.domain.user.enums.BountyRank;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bounty_mission_attempts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BountyMissionAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guild_id", nullable = false)
    private String guildId;

    @Column(name = "mission_id", nullable = false)
    private Long missionId;

    @Column(name = "hunter_discord_user_id", nullable = false)
    private String hunterDiscordUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "hunter_rank", nullable = false)
    private BountyRank hunterRank;

    @Column(name = "reward_granted", nullable = false)
    private boolean rewardGranted;

    @Column(name = "reward_text")
    private String rewardText;

    @Column(name = "contribution_earned", nullable = false)
    private int contributionEarned;

    @Column(name = "promotion_attempt", nullable = false)
    private boolean promotionAttempt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public static BountyMissionAttempt create(String guildId, Long missionId, String hunterDiscordUserId
            , BountyRank hunterRank, boolean rewardGranted, String rewardText, int contributionEarned) {

        BountyMissionAttempt bountyMissionAttempt = new BountyMissionAttempt();
        bountyMissionAttempt.guildId = guildId;
        bountyMissionAttempt.missionId = missionId;
        bountyMissionAttempt.hunterDiscordUserId = hunterDiscordUserId;
        bountyMissionAttempt.hunterRank = hunterRank;
        bountyMissionAttempt.rewardGranted = rewardGranted;
        bountyMissionAttempt.rewardText = rewardText;
        bountyMissionAttempt.contributionEarned = contributionEarned;
        bountyMissionAttempt.promotionAttempt = false;

        return bountyMissionAttempt;
    }
}
