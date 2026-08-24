package com.side.chubotamanju.domain.user.entity;

import com.side.chubotamanju.domain.user.enums.BountySubmissionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bounty_mission_submissions")
@Getter
@Setter
public class BountyMissionSubmission extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guild_id", nullable = false)
    private String guildId;

    @Column(name = "mission_id", nullable = false)
    private Long missionId;

    @Column(name = "hunter_discord_user_id", nullable = false)
    private String hunterDiscordUserId;

    @Column(name = "proof_image_url", length = 1000)
    private String proofImageUrl;

    @Column(name = "proof_text", length = 1000)
    private String proofText;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BountySubmissionStatus status;

    @Column(name = "reviewer_discord_user_id")
    private String reviewerDiscordUserId;

    @Column(name = "review_note", length = 500)
    private String reviewNote;

    @Column(name = "reviewer_votes", length = 2000)
    private String reviewerVotes;

}
