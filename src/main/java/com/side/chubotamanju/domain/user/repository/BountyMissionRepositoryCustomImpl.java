package com.side.chubotamanju.domain.user.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.side.chubotamanju.domain.user.entity.BountyMission;
import com.side.chubotamanju.domain.user.entity.QBountyMission;
import com.side.chubotamanju.domain.user.enums.BountyMissionStatus;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.side.chubotamanju.domain.user.entity.QBountyMission.bountyMission;

public class BountyMissionRepositoryCustomImpl implements BountyMissionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    private JPAQueryFactory jpaQueryFactory;

    @PostConstruct
    public void init() {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    /**
     * 길드에 등록된 미션 목록 조회
     * @param guildId
     * @return
     */
    @Override
    public Slice<BountyMission> findBountyMissionList(String guildId, Long lastBountyMissionId, LocalDateTime lastCreatedAt, Pageable pageable) {
        List<BountyMission> bountyMissionList = jpaQueryFactory.select(bountyMission)
                .from(bountyMission)
                .where(
                        bountyMission.guildId.eq(guildId)
                        , bountyMission.status.eq(BountyMissionStatus.OPEN)
                        , cursorCondition(lastBountyMissionId, lastCreatedAt)
                )
                .orderBy(bountyMission.createdAt.desc(), bountyMission.id.desc())
                .limit(pageable.getPageSize() + 1) //요청 사이즈 보다 1개 더 요청
                .fetch();

        //다음 페이지 존재 여부 확인
        boolean hasNext = false;
        if (bountyMissionList.size() > pageable.getPageSize()) {
            bountyMissionList.remove(pageable.getPageSize()); // 1개 더 가져온 마지막 데이터 제거
            hasNext = true;
        }

        return new SliceImpl<>(bountyMissionList, pageable, hasNext);
    }

    // No-Offset 방식 조건
    private BooleanExpression cursorCondition(Long lastBountyMissionId, LocalDateTime lastCreatedAt) {
        if (lastCreatedAt == null || lastBountyMissionId == null) {
            return null; // 첫 페이지 조회 시에는 조건 없음
        }

        // 조건 1: bountyMission.createdAt < lastCreatedAt
        BooleanExpression isBeforeDate = bountyMission.createdAt.lt(lastCreatedAt);

        // 조건 2: bountyMission.createdAt == lastCreatedAt AND bountyMission.id < lastBountyMissionId
        BooleanExpression isSameDateAndOlderId = bountyMission.createdAt.eq(lastCreatedAt)
                .and(bountyMission.id.lt(lastBountyMissionId));

        // 두 조건을 OR로 묶어줍니다. (날짜가 더 이전이거나, 날짜가 같다면 ID가 더 작거나)
        return isBeforeDate.or(isSameDateAndOlderId);
    }
}
