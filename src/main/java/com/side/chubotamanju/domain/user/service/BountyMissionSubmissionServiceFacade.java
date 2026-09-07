package com.side.chubotamanju.domain.user.service;

import com.side.chubotamanju.domain.r2.service.R2StorageService;
import com.side.chubotamanju.domain.user.entity.*;
import com.side.chubotamanju.domain.user.enums.BountyMissionStatus;
import com.side.chubotamanju.domain.user.enums.BountySubmissionStatus;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionFileServiceDto;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionServiceDto;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionSubmissionServiceDto;
import com.side.chubotamanju.global.event.dto.MissionSubmissionApproveReviewEvent;
import com.side.chubotamanju.global.event.dto.MissionSubmissionRegisterEvent;
import com.side.chubotamanju.global.event.dto.MissionSubmissionRejectReviewEvent;
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
    private final BountyMissionAttemptService bountyMissionAttemptService;
    private final R2StorageService r2StorageService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 미션에 의뢰 보고하기
     * @param discordUserId
     * @param bountyMissionSubmissionServiceDto
     */
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

    /**
     * 미션 의뢰자가 등록한 미션의 보고자들 조회
     * @param missionId
     * @return
     */
    @Transactional(readOnly = true)
    public List<BountyMissionSubmissionServiceDto> findBountyMissionSubmissionByRequestUserIdAndMissionId(String requestUserId, Long missionId) {

        //의뢰 보고자의 길드가 해당 미션이 등록된 길드에 속하는지 확인
        BountyMission mission = bountyMissionService.findByIdAndRequesterDiscordUserId(missionId, requestUserId);

        List<BountyMissionSubmissionServiceDto> bountyMissionSubmissionServiceDtoList = bountyMissionSubmissionService.findBountyMissionSubmissionByMissionId(mission.getId()).stream()
                .map(bountyMissionSubmission -> BountyMissionSubmissionServiceDto.builder()
                        .submissionId(bountyMissionSubmission.getId())
                        .missionId(bountyMissionSubmission.getMissionId())
                        .hunterDiscordUserId(bountyMissionSubmission.getHunterDiscordUserId())
                        .proofText(bountyMissionSubmission.getProofText())
                        .status(bountyMissionSubmission.getStatus())
                        .createdAt(bountyMissionSubmission.getCreatedAt())
                        .bountyMissionFileServiceDtoList(
                                bountyMissionSubmission.getBountyMissionFileList().stream()
                                        .map(bountyMissionFile -> BountyMissionFileServiceDto.builder()
                                                .submissionId(bountyMissionFile.getBountyMissionSubmission().getId())
                                                .originFileName(bountyMissionFile.getOriginFileName())
                                                .fileUrl(r2StorageService.generateObjectAccessUrl(bountyMissionFile.getFileUrl()))
                                                .build())
                                        .toList()
                        )
                        .build())
                .toList();

        return bountyMissionSubmissionServiceDtoList;
    }

    /**
     * 의뢰자가 보고된 제출을 검토한다.
     * @param guildId
     * @param reviewerDiscordUserId
     * @param submissionId
     * @param approve
     * @param note
     */
    @Transactional
    public void reviewSubmission(String guildId, String reviewerDiscordUserId, Long submissionId, String note, boolean approve) {

        //길드에 접수된 보고 조회
        BountyMissionSubmission submission = bountyMissionSubmissionService.findByBountyMissionSubmissionBy(submissionId, guildId, BountySubmissionStatus.PENDING);
        //의뢰자가 거부한 검토 처리
        if(!approve) {
            submission.reject();
            eventPublisher.publishEvent(new MissionSubmissionRejectReviewEvent(List.of(BountyMissionSubmissionServiceDto.from(submission))));
            return;
        }

        //의뢰자가 완료한 검토 처리
        BountyMission mission = bountyMissionService.findByIdAndRequesterDiscordUserId(submission.getMissionId(), reviewerDiscordUserId);
        if (mission.getStatus() != BountyMissionStatus.OPEN || mission.getRewardRemaining() <= 0) {
            throw new IllegalArgumentException("현재 승인 가능한 일반 미션이 아니야.");
        }

        //보고 제출여부 확인
        String hunterDiscordUserId = submission.getHunterDiscordUserId();
        BountyMissionAttempt bountyMissionAttempt = bountyMissionAttemptService.findBountyMissionAttemptBy(mission.getId(), hunterDiscordUserId);
        if (bountyMissionAttempt != null) {
            throw new IllegalArgumentException("이미 클리어 확정 처리된 미션이야.");
        }

        BountyHunterProfile profile = bountyHunterProfileService.findBountyHunterProfileBy(guildId, hunterDiscordUserId);

        //현재 미션 랭크의 점수를 가져와서 헌터 점수 올리기
        boolean rewardGranted = !profile.getCurrentRank().isHigherThan(mission.getMissionRank()); //수행한 미션 랭크보다 헌터 랭크가 높으면 리워드 못받음(형식상 데이터 처리)
        int contributionEarned = mission.getMissionRank().getContributionReward(); //미션 랭크에 부여된 점수
        profile.addContribution(contributionEarned);

        //보고에 검토 정보 추가
        submission.review(reviewerDiscordUserId, note, BountySubmissionStatus.APPROVED);

        //미션 보수 차감
        mission.decreaseOneRewardCount();

        //미션에 보고된 다른 제출들 거부 및 미션 상태 완료 처리
        if (mission.getRewardRemaining() <= 0) {
            List<BountyMissionSubmissionServiceDto> bountyMissionSubmissionRejecteList = findBountyMissionSubmissionDtoBy(mission, hunterDiscordUserId, BountySubmissionStatus.PENDING)
                    .stream()
                    .peek(bountyMissionSubmission -> {
                        bountyMissionSubmission.review(
                                reviewerDiscordUserId
                                , note == null || note.isBlank() ? "선착순 완료로 자동 실패" : note
                                , BountySubmissionStatus.REJECTED
                        );
                    })
                    .map(bountyMissionSubmission -> BountyMissionSubmissionServiceDto.builder()
                            .submissionId(bountyMissionSubmission.getId())
                            .missionId(bountyMissionSubmission.getMissionId())
                            .hunterDiscordUserId(bountyMissionSubmission.getHunterDiscordUserId())
                            .proofText(bountyMissionSubmission.getProofText())
                            .status(bountyMissionSubmission.getStatus())
                            .createdAt(bountyMissionSubmission.getCreatedAt())
                            .build()
                    )
                    .toList();

            // 거부 유저가 있으면 알림처리
            if(!bountyMissionSubmissionRejecteList.isEmpty()) {
                eventPublisher.publishEvent(new MissionSubmissionRejectReviewEvent(bountyMissionSubmissionRejecteList));
            }
            mission.complete(hunterDiscordUserId);
        }

        //헌터의 미션 보고 이력 등록
        bountyMissionAttemptService.createBountyMissionAttempt(guildId, mission.getId(), hunterDiscordUserId
                , profile.getCurrentRank()
                , rewardGranted, mission.getRewardText(), contributionEarned);

        eventPublisher.publishEvent(new MissionSubmissionApproveReviewEvent(BountyMissionSubmissionServiceDto.from(submission)));
    }

    /**
     * 다른 미션 실패처리(선착순 방식의 미션 사용 시 동작)
     * @param mission
     * @param approvedHunterDiscordUserId
     * @param bountySubmissionStatus
     * @return
     */
    private List<BountyMissionSubmission> findBountyMissionSubmissionDtoBy(BountyMission mission, String approvedHunterDiscordUserId, BountySubmissionStatus bountySubmissionStatus) {
        return bountyMissionSubmissionService.findBountyMissionSubmissionBy(approvedHunterDiscordUserId, mission.getId(), bountySubmissionStatus);
    }
}
