package com.side.chubotamanju.domain.user.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.side.chubotamanju.domain.user.entity.BountyMissionAttempt;
import com.side.chubotamanju.domain.user.repository.dto.BountyMissionAttemptSearchCondition;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static com.side.chubotamanju.domain.user.entity.QBountyMissionAttempt.bountyMissionAttempt;


public class BountyMissionAttemptRepositoryCustomImpl implements BountyMissionAttemptRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    private JPAQueryFactory jpaQueryFactory;

    @PostConstruct
    public void init() {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Slice<BountyMissionAttempt> findBountyMissionAttemptList(BountyMissionAttemptSearchCondition bountyMissionAttemptSearchCondition, Pageable pageable) {
        List<BountyMissionAttempt> bountyMissionAttemptList = jpaQueryFactory.select(bountyMissionAttempt)
                .from(bountyMissionAttempt)
                .where(
                        bountyMissionAttempt.guildId.eq(bountyMissionAttemptSearchCondition.getGuildId())
                        , myListFilter(bountyMissionAttemptSearchCondition.getHunterDiscordUserId())
                        , cursorCondition(bountyMissionAttemptSearchCondition.getLastBountyMissionAttemptId(), bountyMissionAttemptSearchCondition.getLastCreatedAt())
                )
                .orderBy(bountyMissionAttempt.createdAt.desc(), bountyMissionAttempt.id.desc())
                .limit(pageable.getPageSize() + 1) //요청 사이즈 보다 1개 더 요청
                .fetch();

        //다음 페이지 존재 여부 확인
        boolean hasNext = false;
        if (bountyMissionAttemptList.size() > pageable.getPageSize()) {
            bountyMissionAttemptList.remove(pageable.getPageSize()); // 1개 더 가져온 마지막 데이터 제거
            hasNext = true;
        }

        return new SliceImpl<>(bountyMissionAttemptList, pageable, hasNext);
    }


    /**
     * 유저 ID가 있을 때는 해당 유저가 등록한 미션만 조회
     * - 서비스에서 전체 조회를 의도해 null을 넘겼다면 이 조건은 무시(pass)
     * - 특정 페이지 조회를 의도해 유저 ID를 채워 보냈다면 해당 유저의 미션만 필터링
     * @param requesterDiscordUserId
     * @return
     */
    private BooleanExpression myListFilter(String requesterDiscordUserId) {
        return requesterDiscordUserId != null
                ? bountyMissionAttempt.hunterDiscordUserId.eq(requesterDiscordUserId) : null;
    }

    // No-Offset 방식 페이징 조건
    private BooleanExpression cursorCondition(Long lastBountyMissionId, LocalDateTime lastCreatedAt) {
        if (lastCreatedAt == null || lastBountyMissionId == null) {
            return null; // 첫 페이지 조회 시에는 조건 없음
        }

        // 조건 1: bountyMission.createdAt < lastCreatedAt
        BooleanExpression isBeforeDate = bountyMissionAttempt.createdAt.lt(lastCreatedAt);

        // 조건 2: bountyMission.createdAt == lastCreatedAt (같은 날짜에 데이터 여러 개 있으면 해당 데이터도 표시)
        // AND bountyMission.id < lastBountyMissionId
        BooleanExpression isSameDateAndOlderId = bountyMissionAttempt.createdAt.eq(lastCreatedAt)
                .and(bountyMissionAttempt.id.lt(lastBountyMissionId));

        // 두 조건을 OR로 묶어줍니다. (날짜가 더 이전이거나, 날짜가 같다면 ID가 더 작거나)
        return isBeforeDate.or(isSameDateAndOlderId);
    }
}
