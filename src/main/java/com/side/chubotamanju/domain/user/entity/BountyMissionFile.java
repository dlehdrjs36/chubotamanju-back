package com.side.chubotamanju.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "bounty_mission_file")
@Getter
public class BountyMissionFile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "origin_file_name", length = 255)
    private String originFileName;

    @Column(name = "file_url", length = 1000)
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    private BountyMissionSubmission bountyMissionSubmission;

    /**
     * [연관관계 편의 메서드]
     * 부모와 자식 객체의 양방향 관계를 모두 설정
     */
    public void addBountyMissionSubmission(BountyMissionSubmission bountyMissionSubmission) {
        this.bountyMissionSubmission = bountyMissionSubmission;
    }

    /**
     * 미션 파일 생성
     * @return
     */
    public static BountyMissionFile create(String originFileName, String fileUrl) {
        BountyMissionFile missionFile = new BountyMissionFile();
        missionFile.originFileName = originFileName;
        missionFile.fileUrl = fileUrl;

        return missionFile;
    }
}
