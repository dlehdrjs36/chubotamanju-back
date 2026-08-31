package com.side.chubotamanju.domain.user.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.side.chubotamanju.domain.user.entity.BountyMission;
import com.side.chubotamanju.domain.user.enums.BountyMissionStatus;
import com.side.chubotamanju.domain.user.repository.dto.BountyMissionSearchCondition;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

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
     * @param bountyMissionSearchCondition
     * @param pageable
     * @return
     */
    @Override
    public Slice<BountyMission> findBountyMissionList(BountyMissionSearchCondition bountyMissionSearchCondition, Pageable pageable) {
        List<BountyMission> bountyMissionList = jpaQueryFactory.select(bountyMission)
                .from(bountyMission)
                .where(
                        bountyMission.guildId.eq(bountyMissionSearchCondition.getGuildId())
                        , myListFilter(bountyMissionSearchCondition.getRequesterDiscordUserId())
                        , cursorCondition(bountyMissionSearchCondition.getLastBountyMissionId(), bountyMissionSearchCondition.getLastCreatedAt())
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

    /**
     * 유저 ID가 있을 때는 해당 유저가 등록한 미션만 조회
     * - 서비스에서 전체 조회를 의도해 null을 넘겼다면 이 조건은 무시(pass)
     * - 특정 페이지 조회를 의도해 유저 ID를 채워 보냈다면 해당 유저의 미션만 필터링
     * @param requesterDiscordUserId
     * @return
     */
    private BooleanExpression myListFilter(String requesterDiscordUserId) {
        return requesterDiscordUserId != null
                ? bountyMission.requesterDiscordUserId.eq(requesterDiscordUserId)
                : bountyMission.status.eq(BountyMissionStatus.OPEN);
    }

    // No-Offset 방식 페이징 조건
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
