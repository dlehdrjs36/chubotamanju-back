package com.side.chubotamanju.domain.user.entity;

import com.side.chubotamanju.domain.user.enums.BountySubmissionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bounty_mission_submissions")
@Getter
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bountyMissionSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BountyMissionFile> bountyMissionFileList = new ArrayList<>();

    /**
     * [연관관계 편의 메서드]
     * 부모와 자식 객체의 양방향 관계를 모두 설정
     */
    public void addBountyMissionFile(BountyMissionFile missionFile) {
        // 1. 부모 객체(1)의 리스트에 자식 객체(N)를 추가
        this.bountyMissionFileList.add(missionFile);

        // 2. 자식 객체(N)에게도 부모 객체(1)가 누구인지 알려줍니다. (this = 현재 부모 객체)
        missionFile.addBountyMissionSubmission(this);
    }

    /**
     * 미션 보고 생성
     * @return
     */
    public static BountyMissionSubmission create(String guildId, Long missionId, String hunterDiscordUserId, String proofText) {
        BountyMissionSubmission bountyMissionSubmission = new BountyMissionSubmission();
        bountyMissionSubmission.guildId = guildId;
        bountyMissionSubmission.missionId = missionId;
        bountyMissionSubmission.hunterDiscordUserId = hunterDiscordUserId;
        bountyMissionSubmission.proofText = proofText;
        bountyMissionSubmission.status = BountySubmissionStatus.PENDING;

        return bountyMissionSubmission;
    }

}
