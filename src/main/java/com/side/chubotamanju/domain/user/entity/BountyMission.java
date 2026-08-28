package com.side.chubotamanju.domain.user.entity;

import com.side.chubotamanju.domain.user.enums.BountyMissionStatus;
import com.side.chubotamanju.domain.user.enums.BountyRank;
import com.side.chubotamanju.domain.user.enums.TeamClearRule;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Table(name = "bounty_missions")
@Getter
public class BountyMission extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guild_id", nullable = false)
    private String guildId;

    @Column(name = "requester_discord_user_id", nullable = false)
    private String requesterDiscordUserId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "mission_rank", nullable = false)
    private BountyRank missionRank;

    @Column(name = "reward_text")
    private String rewardText;

    @Column(name = "promotion_only", nullable = false)
    private boolean promotionOnly;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BountyMissionStatus status;

    @Column(name = "completed_by_discord_user_id")
    private String completedByDiscordUserId;

    @Column(name = "reward_limit", nullable = false)
    private int rewardLimit;

    @Column(name = "reward_remaining", nullable = false)
    private int rewardRemaining;

    @Column(name = "team_a_member_ids", length = 1000)
    private String teamAMemberIds;

    @Column(name = "team_b_member_ids", length = 1000)
    private String teamBMemberIds;

    @Column(name = "target_member_ids", length = 1000)
    private String targetMemberIds;

    @Enumerated(EnumType.STRING)
    @Column(name = "team_clear_rule", length = 30)
    private TeamClearRule teamClearRule;

    @Column(name = "team_clear_threshold")
    private Integer teamClearThreshold;

    @Column(name = "min_rating")
    private BigDecimal minRating;

    /**
     * 의뢰취소
     */
    public void cancle() {
        this.status = BountyMissionStatus.CANCLE;
    }

    // 미션 생성
    public static BountyMission create(String guildId, String title, String requesterDiscordUserId, String rewardText, int rewardLimit, String description) {
        BountyMission bountyMission = new BountyMission();
        bountyMission.guildId = guildId;
        bountyMission.title = title;
        bountyMission.requesterDiscordUserId = requesterDiscordUserId;
        bountyMission.rewardText = rewardText;
        bountyMission.rewardLimit = rewardLimit;
        bountyMission.description = description;
        bountyMission.rewardRemaining = rewardLimit;
        bountyMission.status = BountyMissionStatus.OPEN;
        bountyMission.missionRank = BountyRank.F;
        bountyMission.promotionOnly = false;

        return bountyMission;
    }

}
