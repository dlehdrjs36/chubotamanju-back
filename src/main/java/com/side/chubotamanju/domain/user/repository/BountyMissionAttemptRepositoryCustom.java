package com.side.chubotamanju.domain.user.repository;

import com.side.chubotamanju.domain.user.entity.BountyMissionAttempt;
import com.side.chubotamanju.domain.user.repository.dto.BountyMissionAttemptSearchCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface BountyMissionAttemptRepositoryCustom {
    Slice<BountyMissionAttempt> findBountyMissionAttemptList(BountyMissionAttemptSearchCondition bountyMissionAttemptSearchCondition, Pageable pageable);
}
