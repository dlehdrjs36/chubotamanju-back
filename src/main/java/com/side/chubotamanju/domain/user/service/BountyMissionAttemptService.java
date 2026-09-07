package com.side.chubotamanju.domain.user.service;

import com.side.chubotamanju.domain.user.entity.BountyMissionAttempt;
import com.side.chubotamanju.domain.user.enums.BountyRank;
import com.side.chubotamanju.domain.user.repository.BountyMissionAttemptRepository;
import com.side.chubotamanju.domain.user.repository.dto.BountyMissionAttemptSearchCondition;
import com.side.chubotamanju.domain.user.repository.dto.BountyMissionSearchCondition;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionAttemptServiceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BountyMissionAttemptService {

    private final BountyMissionAttemptRepository bountyMissionAttemptRepository;

    /**
     * 헌터의 미션 시도 정보 조회
     * @param missionId
     * @param hunterDiscordUserId
     * @return
     */
    public BountyMissionAttempt findBountyMissionAttemptBy(Long missionId, String hunterDiscordUserId) {
        return bountyMissionAttemptRepository.findByMissionIdAndHunterDiscordUserId(missionId, hunterDiscordUserId)
                .orElseGet(() -> null);
    }

    /**
     * 헌터의 미션 시도 목록 조회
     * @param hunterDiscordUserId
     * @return
     */
    public List<BountyMissionAttemptServiceDto> searchBountyMissionAttemptsBy(String guildId, String hunterDiscordUserId, Long lastBountyMissionId, LocalDateTime lastCreatedAt, Pageable pageable) {
        BountyMissionAttemptSearchCondition bountyMissionSearchCondition = BountyMissionAttemptSearchCondition.builder()
                .guildId(guildId)
                .hunterDiscordUserId(hunterDiscordUserId)
                .lastBountyMissionAttemptId(lastBountyMissionId)
                .lastCreatedAt(lastCreatedAt)
                .build();

        return bountyMissionAttemptRepository.findBountyMissionAttemptList(bountyMissionSearchCondition, pageable)
                .stream()
                .map(BountyMissionAttemptServiceDto::from)
                .toList();
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
