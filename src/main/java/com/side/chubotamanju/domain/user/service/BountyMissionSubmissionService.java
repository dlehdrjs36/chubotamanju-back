package com.side.chubotamanju.domain.user.service;

import com.side.chubotamanju.domain.user.entity.BountyMission;
import com.side.chubotamanju.domain.user.entity.BountyMissionSubmission;
import com.side.chubotamanju.domain.user.enums.BountySubmissionStatus;
import com.side.chubotamanju.domain.user.repository.BountyMissionRepository;
import com.side.chubotamanju.domain.user.repository.BountyMissionSubmissionRepository;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionFileServiceDto;
import com.side.chubotamanju.domain.user.service.validator.BountyMissionSubmissionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BountyMissionSubmissionService {
    private final BountyMissionRepository bountyMissionRepository;
    private final BountyMissionSubmissionRepository bountyMissionSubmissionRepository;
    private final BountyMissionSubmissionValidator bountyMissionSubmissionValidator;

    /**
     * 길드의 특정 미션에 대하여 미션 보고 등록
     * @param discordUserGuildIdList
     * @param hunterDiscordUserId
     * @param missionId
     * @param bountyMissionFileServiceDtoList
     * @param proofText
     */
    @Transactional
    public BountyMissionSubmission submitMissionProof(List<String> discordUserGuildIdList, String hunterDiscordUserId, Long missionId, List<BountyMissionFileServiceDto> bountyMissionFileServiceDtoList, String proofText) {

        //의뢰 보고자의 길드가 해당 미션이 등록된 길드에 속하는지 확인
        BountyMission mission = bountyMissionRepository.findByIdAndGuildIdIn(missionId, discordUserGuildIdList)
                .orElseThrow(() -> new IllegalArgumentException("해당 의뢰를 찾을 수 없어."));

        // BountyMissionSubmission 생성 검증
        bountyMissionSubmissionValidator.validate(mission, hunterDiscordUserId, bountyMissionFileServiceDtoList, proofText);

        // BountyMissionSubmission 생성
        BountyMissionSubmission bountyMissionSubmission = BountyMissionSubmission.create(mission.getGuildId(), missionId, hunterDiscordUserId, proofText);

        return bountyMissionSubmissionRepository.save(bountyMissionSubmission);
    }

    /**
     * 길드에 접수된 보고 조회
     * @param submissionId
     * @param guildId
     * @return
     */
    public BountyMissionSubmission findByBountyMissionSubmissionBy(Long submissionId, String guildId, BountySubmissionStatus bountySubmissionStatus) {
        return bountyMissionSubmissionRepository.findByIdAndGuildIdAndStatus(submissionId, guildId, bountySubmissionStatus)
                .orElseThrow(() -> new IllegalArgumentException("길드에서 해당 의뢰 보고를 찾을 수 없거나 이미 검토가 완료되었습니다."));
    }

    /**
     * 미션에 제출된 보고 목록 조회
     * @param missionId
     * @return
     */
    public List<BountyMissionSubmission> findBountyMissionSubmissionByMissionId(Long missionId) {
        return bountyMissionSubmissionRepository.findByMissionId(missionId);
    }

    /**
     * 현재 미션 수행 완료한 헌터를 제외한 미션에 제출된 대기 상태의 보고 목록 조회
     * @param missionId
     * @param bountySubmissionStatus
     * @return
     */
    public List<BountyMissionSubmission> findBountyMissionSubmissionBy(String approvedHunterDiscordUserId, Long missionId, BountySubmissionStatus bountySubmissionStatus) {
        return bountyMissionSubmissionRepository.findByHunterDiscordUserIdIsNotAndMissionIdAndStatus(approvedHunterDiscordUserId, missionId, bountySubmissionStatus);
    }
}
