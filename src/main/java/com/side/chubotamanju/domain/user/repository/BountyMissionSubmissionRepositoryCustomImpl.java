package com.side.chubotamanju.domain.user.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.side.chubotamanju.domain.bounty.controller.dto.api.BountyPendingSubmissionDto;
import com.side.chubotamanju.domain.user.entity.*;
import com.side.chubotamanju.domain.user.enums.BountySubmissionStatus;
import com.side.chubotamanju.domain.user.service.BountyMissionSubmissionService;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionSubmissionQueryDto;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionFileServiceDto;
import com.side.chubotamanju.global.common.PagingRequest;
import com.side.chubotamanju.global.common.PagingResponse;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.side.chubotamanju.domain.user.entity.QBountyMission.bountyMission;
import static com.side.chubotamanju.domain.user.entity.QBountyMissionAttempt.bountyMissionAttempt;
import static com.side.chubotamanju.domain.user.entity.QBountyMissionFile.bountyMissionFile;
import static com.side.chubotamanju.domain.user.entity.QBountyMissionSubmission.bountyMissionSubmission;

public class BountyMissionSubmissionRepositoryCustomImpl implements BountyMissionSubmissionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    private JPAQueryFactory jpaQueryFactory;

    @PostConstruct
    public void init() {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    /**
     * 길드에서 내가 제출한 미션 목록 조회(서비스 내부)
     * @param guildId
     * @param hunterDiscordUserId
     * @param pagingRequest
     * @return
     */
    @Override
    public PagingResponse<BountyMissionSubmission> findBountyMissionSubmissionList(String guildId, String hunterDiscordUserId, PagingRequest pagingRequest) {
        List<BountyMissionSubmission> bountyMissionSubmissionList = jpaQueryFactory.select(bountyMissionSubmission)
                .from(bountyMissionSubmission)
                .where(
                        bountyMissionSubmission.guildId.eq(guildId)
                        , bountyMissionSubmission.status.eq(BountySubmissionStatus.PENDING)
                        , bountyMissionSubmission.hunterDiscordUserId.eq(hunterDiscordUserId)
//                            , cursorCondition(bountyMissionSearchCondition.getLastBountyMissionId(), bountyMissionSearchCondition.getLastCreatedAt())
                )
                .offset(pagingRequest.isOffsetMode() ? (long) (pagingRequest.getPage() - 1) * pagingRequest.getPageSize() : 0L)
                .orderBy(bountyMissionSubmission.createdAt.desc(), bountyMissionSubmission.id.desc())
                .limit(pagingRequest.getPageSize() + 1) //요청 사이즈 보다 1개 더 요청
                .fetch();

        //다음 페이지 존재 여부 확인
        boolean hasNext = false;
        if (bountyMissionSubmissionList.size() > pagingRequest.getPageSize()) {
            bountyMissionSubmissionList.remove(pagingRequest.getPageSize()); // 1개 더 가져온 마지막 데이터 제거
            hasNext = true;
        }

        // 페이징 모드일 때만 Count 쿼리 실행 후 Page 기반 응답 생성
        if (pagingRequest.isOffsetMode()) {
            Long totalCount = jpaQueryFactory
                    .select(bountyMissionSubmission.count())
                    .from(bountyMissionSubmission)
                    .where(
                            bountyMissionSubmission.guildId.eq(guildId)
                            , bountyMissionSubmission.status.eq(BountySubmissionStatus.PENDING)
                            , bountyMissionSubmission.hunterDiscordUserId.eq(hunterDiscordUserId)
//                            , cursorCondition(bountyMissionSearchCondition.getLastBountyMissionId(), bountyMissionSearchCondition.getLastCreatedAt())
                    )
                    .fetchOne();

            long total = totalCount != null ? totalCount : 0L;
            int totalPages = (int) Math.ceil((double) total / pagingRequest.getPageSize());

            return new PagingResponse<>(bountyMissionSubmissionList, hasNext, pagingRequest.getPage(), pagingRequest.getPageSize(), totalPages);
        }

        return new PagingResponse<>(bountyMissionSubmissionList, hasNext);
    }

    /**
     * 길드에서 내가 제출한 미션 목록 조회(웹, 봇)
     * - 봇 : 오프셋 방식
     * - 웹 : 무한스크롤 방식
     * @param searchCondition
     * @param pagingRequest
     * @return
     */
    @Override
    public PagingResponse<BountyMissionSubmissionQueryDto> findBountyMissionSubmissionListView(BountyMissionSubmissionService.BountyMissionSubmissionSearchCondition searchCondition, PagingRequest pagingRequest) {
        List<BountyMissionSubmissionQueryDto> bountyMissionSubmissionList = jpaQueryFactory.select(
                    Projections.fields(BountyMissionSubmissionQueryDto.class
                                    , bountyMissionSubmission.id.as("submissionId")
                                    , bountyMissionSubmission.mission.id.as("missionId")
                                    , ExpressionUtils.as(
                                            JPAExpressions.select(bountyMission.title)
                                                    .from(bountyMission)
                                                    .where(bountyMission.id.eq(bountyMissionSubmission.mission.id))
                                            , "missionTitle")
                                    , bountyMissionSubmission.hunterDiscordUserId.as("requesterDiscordUserId")
                                    , ExpressionUtils.as(
                                            JPAExpressions.select(bountyMission.rewardText)
                                                    .from(bountyMission)
                                                    .where(bountyMission.id.eq(bountyMissionSubmission.mission.id))
                                            , "rewardText")
                                    , bountyMissionSubmission.proofText
                                    , bountyMissionSubmission.status
                                    , bountyMissionSubmission.reviewNote
                                    , bountyMissionSubmission.createdAt
                ))
                .from(bountyMissionSubmission)
                .where(
                        bountyMissionSubmission.guildId.eq(searchCondition.guildId())
                        , bountyMissionSubmission.status.eq(BountySubmissionStatus.PENDING)
                        , bountyMissionSubmission.hunterDiscordUserId.eq(searchCondition.hunterDiscordUserId())
                            , cursorCondition(searchCondition.lastBountyMissionSubmissionId(), searchCondition.lastCreatedAt())
                )
                .offset(pagingRequest.isOffsetMode() ? (long) (pagingRequest.getPage() - 1) * pagingRequest.getPageSize() : 0L)
                .orderBy(bountyMissionSubmission.createdAt.desc(), bountyMissionSubmission.id.desc())
                .limit(pagingRequest.getPageSize() + 1) //요청 사이즈 보다 1개 더 요청
                .fetch();

        //다음 페이지 존재 여부 확인
        boolean hasNext = false;
        if (bountyMissionSubmissionList.size() > pagingRequest.getPageSize()) {
            bountyMissionSubmissionList.remove(pagingRequest.getPageSize()); // 1개 더 가져온 마지막 데이터 제거
            hasNext = true;
        }

        // 페이징 모드일 때만 Count 쿼리 실행 후 Page 기반 응답 생성
        if (pagingRequest.isOffsetMode()) {
            Long totalCount = jpaQueryFactory
                    .select(bountyMissionSubmission.count())
                    .from(bountyMissionSubmission)
                    .where(
                            bountyMissionSubmission.guildId.eq(searchCondition.guildId())
                            , bountyMissionSubmission.status.eq(BountySubmissionStatus.PENDING)
                            , bountyMissionSubmission.hunterDiscordUserId.eq(searchCondition.hunterDiscordUserId())
                    )
                    .fetchOne();

            long total = totalCount != null ? totalCount : 0L;
            int totalPages = (int) Math.ceil((double) total / pagingRequest.getPageSize());

            return new PagingResponse<>(bountyMissionSubmissionList, hasNext, pagingRequest.getPage(), pagingRequest.getPageSize(), totalPages);
        }
        return new PagingResponse<>(bountyMissionSubmissionList, hasNext);
    }

    /**
     * 길드에서 내가 등록한 미션에 들어온 보고 목록 조회(화면 전용 - 봇)
     * @param guildId
     * @param hunterDiscordUserId
     * @param pagingRequest
     * @return
     */
    @Override
    public PagingResponse<BountyPendingSubmissionDto> findBountyPendingSubmissionDtoView(String guildId, String hunterDiscordUserId, PagingRequest pagingRequest) {

        List<BountyPendingSubmissionDto> bountyPendingSubmissionDtoList = jpaQueryFactory.select(
                        Projections.fields(BountyPendingSubmissionDto.class
                                , bountyMissionSubmission.id.as("submissionId")
                                , bountyMissionSubmission.mission.id.as("missionId")
                                , bountyMissionSubmission.mission.title.as("missionTitle")
                                , bountyMissionSubmission.hunterDiscordUserId.as("hunterDiscordUserId")
                                , bountyMissionSubmission.mission.rewardText.as("rewardText")
//                                , bountyMissionSubmission.proofText
//                                , bountyMissionSubmission.proofImageUrl
                                , bountyMissionSubmission.createdAt.as("submittedAt")
                        ))
                .from(bountyMissionSubmission)
                .join(bountyMissionSubmission.mission, bountyMission)
                .where(
                        bountyMissionSubmission.guildId.eq(guildId)
                        , bountyMissionSubmission.status.eq(BountySubmissionStatus.PENDING)
                        , bountyMissionSubmission.mission.requesterDiscordUserId.eq(hunterDiscordUserId)
//                            , cursorCondition(bountyMissionSearchCondition.getLastBountyMissionId(), bountyMissionSearchCondition.getLastCreatedAt())
                )
                .offset(pagingRequest.isOffsetMode() ? (long) (pagingRequest.getPage() - 1) * pagingRequest.getPageSize() : 0L)
                .orderBy(bountyMissionSubmission.createdAt.desc(), bountyMissionSubmission.id.desc())
                .limit(pagingRequest.getPageSize() + 1) //요청 사이즈 보다 1개 더 요청
                .fetch();

        //다음 페이지 존재 여부 확인
        boolean hasNext = false;
        if (bountyPendingSubmissionDtoList.size() > pagingRequest.getPageSize()) {
            bountyPendingSubmissionDtoList.remove(pagingRequest.getPageSize()); // 1개 더 가져온 마지막 데이터 제거
            hasNext = true;
        }

        //제출별 파일 목록 설정
        List<Long> submissionIdList = bountyPendingSubmissionDtoList.stream()
            .map(BountyPendingSubmissionDto::getSubmissionId)
            .toList();

        List<BountyMissionFile> bountyMissionFileListAll = jpaQueryFactory.select(bountyMissionFile)
                .from(bountyMissionFile)
                .where(bountyMissionFile.bountyMissionSubmission.id.in(submissionIdList))
                .fetch();

        Map<Long, List<BountyMissionFile>> bountyMissionFileMap = bountyMissionFileListAll.stream()
                .collect(Collectors.groupingBy(bountyMissionFile -> bountyMissionFile.getBountyMissionSubmission().getId()));

        // 보고별 파일 설정
        bountyPendingSubmissionDtoList.forEach(bountyPendingSubmissionDto -> {
            List<BountyMissionFileServiceDto> list = bountyMissionFileMap.getOrDefault(bountyPendingSubmissionDto.getSubmissionId(), List.of())
                    .stream()
                    .map(BountyMissionFileServiceDto::from)
                    .toList();

            bountyPendingSubmissionDto.setBountyMissionFileList(list);
        });


        // 페이징 모드일 때만 Count 쿼리 실행 후 Page 기반 응답 생성
        if (pagingRequest.isOffsetMode()) {
            Long totalCount = jpaQueryFactory
                    .select(bountyMissionSubmission.count())
                    .from(bountyMissionSubmission)
                    .join(bountyMissionSubmission.mission, bountyMission)
                    .where(
                            bountyMissionSubmission.guildId.eq(guildId)
                            , bountyMissionSubmission.status.eq(BountySubmissionStatus.PENDING)
                            , bountyMissionSubmission.mission.requesterDiscordUserId.eq(hunterDiscordUserId)
//                            , cursorCondition(bountyMissionSearchCondition.getLastBountyMissionId(), bountyMissionSearchCondition.getLastCreatedAt())
                    )
                    .fetchOne();

            long total = totalCount != null ? totalCount : 0L;
            int totalPages = (int) Math.ceil((double) total / pagingRequest.getPageSize());

//            스프링 데이터 Page을 활용해 PagingResponse 조립 (0 페이지 부터 시작하므로 -1 처리 필요)
            return new PagingResponse<>(bountyPendingSubmissionDtoList, hasNext, pagingRequest.getPage(), pagingRequest.getPageSize(), totalPages);
        }
        return new PagingResponse<>(bountyPendingSubmissionDtoList, hasNext);
    }

    /**
     * No-Offset 방식 페이징
     * @param lastId
     * @param lastCreatedAt
     * @return
     */
    private BooleanExpression cursorCondition(Long lastId, LocalDateTime lastCreatedAt) {

        if (lastCreatedAt == null || lastId == null) {
            return null; // 첫 페이지 조회 시에는 조건 없음
        }

        // 조건 1: bountyMission.createdAt < lastCreatedAt
        BooleanExpression isBeforeDate = bountyMissionAttempt.createdAt.lt(lastCreatedAt);

        // 조건 2: bountyMission.createdAt == lastCreatedAt (같은 날짜에 데이터 여러 개 있으면 해당 데이터도 표시)
        // AND bountyMission.id < lastId
        BooleanExpression isSameDateAndOlderId = bountyMissionAttempt.createdAt.eq(lastCreatedAt)
                .and(bountyMissionAttempt.id.lt(lastId));

        // 두 조건을 OR로 묶어줍니다. (날짜가 더 이전이거나, 날짜가 같다면 ID가 더 작거나)
        return isBeforeDate.or(isSameDateAndOlderId);
    }
}
