package com.side.chubotamanju.domain.bounty.controller.dto.api;

import com.side.chubotamanju.domain.user.enums.BountyRank;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 접수된 의뢰보고 듣기 - 완료, 거부 버튼으로 검토 api
 */
@Getter
@Builder
public class BountySubmissionReviewResult {
    private Long submissionId;
    private boolean approved;
    private Long missionId;
    private String missionTitle;
    private String hunterDiscordUserId;
    private int contributionEarned;
    private boolean rewardGranted;
    private String rewardText;
    private boolean promotionUnlocked;
    private BountyRank pendingPromotionRank;
    private int rewardRemaining;
    private boolean missionCompleted;
    private List<String> autoRejectedHunterDiscordUserIds;
    private boolean promotionMission;
    private boolean reviewFinalized;
    private int reviewerApproveCount;
    private int reviewerRejectCount;
    private int reviewerRequiredApproveCount;
}
