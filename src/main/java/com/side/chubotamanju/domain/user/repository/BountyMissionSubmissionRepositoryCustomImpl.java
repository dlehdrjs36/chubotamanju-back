package com.side.chubotamanju.domain.user.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.side.chubotamanju.domain.bounty.controller.dto.api.BountyHunterSubmissionDto;
import com.side.chubotamanju.domain.user.entity.BountyMission;
import com.side.chubotamanju.domain.user.entity.BountyMissionSubmission;
import com.side.chubotamanju.domain.user.entity.QBountyMissionSubmission;
import com.side.chubotamanju.domain.user.enums.BountyMissionStatus;
import com.side.chubotamanju.domain.user.enums.BountySubmissionStatus;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionSubmissionServiceDto;
import com.side.chubotamanju.global.common.PagingRequest;
import com.side.chubotamanju.global.common.PagingResponse;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

import static com.side.chubotamanju.domain.user.entity.QBountyMission.bountyMission;
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

//            스프링 데이터 Page을 활용해 PagingResponse 조립 (0 페이지 부터 시작하므로 -1 처리 필요)
//            Page<BountyMission> pageResult = new PageImpl<>(bountyMissionList, PageRequest.of(pageable.getPage() - 1, pageable.getPageSize()), total);
            return new PagingResponse<>(bountyMissionSubmissionList, hasNext, pagingRequest.getPage(), pagingRequest.getPageSize(), totalPages);
        }

//        SliceImpl<BountyMission> bountyMissionSlice = new SliceImpl<>(bountyMissionList, PageRequest.of(0, pageable.getPageSize()), hasNext);
        return new PagingResponse<>(bountyMissionSubmissionList, hasNext);
    }

    /**
     * 길드에서 내가 제출한 미션 목록 조회(화면 전용 - 봇)
     * @param guildId
     * @param hunterDiscordUserId
     * @param pagingRequest
     * @return
     */
    @Override
    public PagingResponse<BountyHunterSubmissionDto> findBountyMissionSubmissionListView(String guildId, String hunterDiscordUserId, PagingRequest pagingRequest) {
        List<BountyHunterSubmissionDto> bountyMissionSubmissionList = jpaQueryFactory.select(
                    Projections.fields(BountyHunterSubmissionDto.class
                                    , bountyMissionSubmission.id.as("submissionId")
                                    , bountyMissionSubmission.missionId
                                    , ExpressionUtils.as(
                                            JPAExpressions.select(bountyMission.title)
                                                    .from(bountyMission)
                                                    .where(bountyMission.id.eq(bountyMissionSubmission.missionId))
                                            , "missionTitle")
                                    , bountyMissionSubmission.hunterDiscordUserId.as("requesterDiscordUserId")
                                    , ExpressionUtils.as(
                                            JPAExpressions.select(bountyMission.rewardText)
                                                    .from(bountyMission)
                                                    .where(bountyMission.id.eq(bountyMissionSubmission.missionId))
                                            , "rewardText")
                                    , bountyMissionSubmission.proofText
                                    , bountyMissionSubmission.status
                                    , bountyMissionSubmission.reviewNote
                ))
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

//            스프링 데이터 Page을 활용해 PagingResponse 조립 (0 페이지 부터 시작하므로 -1 처리 필요)
            return new PagingResponse<>(bountyMissionSubmissionList, hasNext, pagingRequest.getPage(), pagingRequest.getPageSize(), totalPages);
        }
        return new PagingResponse<>(bountyMissionSubmissionList, hasNext);
    }
}
