package com.side.chubotamanju.domain.user.service.validator;

import com.side.chubotamanju.domain.user.entity.BountyMission;
import com.side.chubotamanju.domain.user.enums.BountyMissionStatus;
import com.side.chubotamanju.domain.user.enums.BountySubmissionStatus;
import com.side.chubotamanju.domain.user.repository.BountyMissionSubmissionRepository;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionFileServiceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BountyMissionSubmissionValidator {

    private final BountyMissionSubmissionRepository bountyMissionSubmissionRepository;

    public void validate(BountyMission bountyMission, String hunterDiscordUserId, List<BountyMissionFileServiceDto> bountyMissionFileServiceDtoList, String proofText) {

//        if (bountyMission.getRequesterDiscordUserId().equals(hunterDiscordUserId)) {
//            throw new IllegalArgumentException("본인이 올린 의뢰는 본인이 수행보고할 수 없어.");
//        }

        if ((bountyMission.getStatus() != BountyMissionStatus.OPEN) || bountyMission.getRewardRemaining() <= 0) {
            throw new IllegalArgumentException("현재 수행보고 가능한 의뢰가 아니야.");
        }

        boolean isSubmitMission = bountyMissionSubmissionRepository.existsByMissionIdAndHunterDiscordUserIdAndStatus(bountyMission.getId(), hunterDiscordUserId, BountySubmissionStatus.PENDING);
        if (isSubmitMission) {
            throw new IllegalArgumentException("이미 검토 대기 중인 증빙이 있어.");
        }

        if (!StringUtils.hasText(proofText) && bountyMissionFileServiceDtoList.isEmpty()) {
            throw new IllegalArgumentException("의뢰보고는 이미지 또는 텍스트 중 하나 이상 제출해야 해.");
        }
    }
}
