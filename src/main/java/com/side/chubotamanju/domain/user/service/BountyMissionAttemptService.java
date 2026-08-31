package com.side.chubotamanju.domain.user.service;

import com.side.chubotamanju.domain.user.entity.BountyMissionAttempt;
import com.side.chubotamanju.domain.user.enums.BountyRank;
import com.side.chubotamanju.domain.user.repository.BountyMissionAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BountyMissionAttemptService {

    private final BountyMissionAttemptRepository bountyMissionAttemptRepository;

    /**
     * 헌터의 미션 보고조회
     * @param missionId
     * @param hunterDiscordUserId
     * @return
     */
    public BountyMissionAttempt findBountyMissionAttemptBy(Long missionId, String hunterDiscordUserId) {
        return bountyMissionAttemptRepository.findByMissionIdAndHunterDiscordUserId(missionId, hunterDiscordUserId)
                .orElseGet(() -> null);
    }


    /**
     * 헌터의 미션 수행 이력 등록
     * @param guildId
     * @param missionId
     * @param hunterDiscordUserId
     * @param hunterRank
     * @param rewardGranted
     * @param rewardText
     * @param contributionEarned
     */
    @Transactional
    public void createBountyMissionAttempt(String guildId, Long missionId, String hunterDiscordUserId, BountyRank hunterRank, boolean rewardGranted, String rewardText, int contributionEarned) {
        BountyMissionAttempt newBountyMissionAttempt = BountyMissionAttempt.create(guildId, missionId, hunterDiscordUserId, hunterRank, rewardGranted, rewardText, contributionEarned);

        bountyMissionAttemptRepository.save(newBountyMissionAttempt);
    }


}
