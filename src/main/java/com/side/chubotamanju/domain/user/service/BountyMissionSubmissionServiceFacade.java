package com.side.chubotamanju.domain.user.service;

import com.side.chubotamanju.domain.r2.service.R2StorageService;
import com.side.chubotamanju.domain.user.entity.BountyMissionFile;
import com.side.chubotamanju.domain.user.entity.BountyMissionSubmission;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionFileServiceDto;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionServiceDto;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionSubmissionServiceDto;
import com.side.chubotamanju.global.event.dto.MissionSubmissionRegisterEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BountyMissionSubmissionServiceFacade {

    private final BountyMissionService bountyMissionService;
    private final BountyHunterProfileService bountyHunterProfileService;
    private final BountyMissionSubmissionService bountyMissionSubmissionService;
    private final R2StorageService r2StorageService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void submitMission(String discordUserId, BountyMissionSubmissionServiceDto bountyMissionSubmissionServiceDto) {
        // 임시폴더에 업로드한 파일경로 추출
        List<BountyMissionFileServiceDto> bountyMissionFileServiceDtoList = bountyMissionSubmissionServiceDto.getBountyMissionFileServiceDtoList();
        List<String> fileUrlList = bountyMissionFileServiceDtoList.stream()
                .map(bountyMissionFileServiceDto -> bountyMissionFileServiceDto.getFileUrl())
                .toList();

        // 의뢰 보고자의 소속 길드 목록 조회
        List<String> guildIdList = bountyHunterProfileService.findGuildIdList(discordUserId);

        // 미션 의뢰 보고 등록
        BountyMissionSubmission bountyMissionSubmission = bountyMissionSubmissionService.submitMissionProof(guildIdList, discordUserId, bountyMissionSubmissionServiceDto.getMissionId(), bountyMissionFileServiceDtoList, bountyMissionSubmissionServiceDto.getProofText());

        // 추출한 임시폴더 업로드한 파일경로를 이용해서 R2의 tmp 버킷에서 실제 bounty 버킷으로 파일 이동
        BountyMissionServiceDto bountyMissionServiceDto = bountyMissionService.findByMissionId(bountyMissionSubmissionServiceDto.getMissionId());
        Map<String, String> newFileUrlMap = r2StorageService.resolveTempFiles(bountyMissionServiceDto.getGuildId(), fileUrlList);

        // 의뢰 보고의 기존 임시폴더의 경로를 실사용폴더 경로로 변경처리
        bountyMissionFileServiceDtoList.forEach(bountyMissionFileServiceDto -> {
            String newFileUrl = newFileUrlMap.get(bountyMissionFileServiceDto.getFileUrl());
            bountyMissionFileServiceDto.setFileUrl(newFileUrl);
        });

        // bountyMissionSubmission에 대한 BountyMissionFile 파일 목록 생성 및 연관관계 설정
        for (BountyMissionFileServiceDto bountyMissionFileServiceDto : bountyMissionFileServiceDtoList) {
            BountyMissionFile missionFile = BountyMissionFile.create(bountyMissionFileServiceDto.getOriginFileName(), bountyMissionFileServiceDto.getFileUrl());
            bountyMissionSubmission.addBountyMissionFile(missionFile);

        }

        //3. 디스코드 봇 알림 전송. 디스코드 봇으로 미션 등록자에게 DM 메시지 전달(디스코드 봇 api 엔드포인트 추가 필요 - 서버간 암호키를 공유하는 헤더 설정 필요)
        eventPublisher.publishEvent(new MissionSubmissionRegisterEvent(bountyMissionServiceDto));

    }
}
