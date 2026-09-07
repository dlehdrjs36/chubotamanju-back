package com.side.chubotamanju.domain.user.repository;

import com.side.chubotamanju.domain.bounty.controller.MissionController;
import com.side.chubotamanju.domain.user.entity.BountyMission;
import com.side.chubotamanju.domain.user.repository.dto.BountyMissionSearchCondition;
import com.side.chubotamanju.global.common.PagingRequest;
import com.side.chubotamanju.global.common.PagingResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

public interface BountyMissionRepositoryCustom {
    Slice<BountyMission> findBountyMissionList(BountyMissionSearchCondition bountyMissionSearchCondition, Pageable pageable);
    PagingResponse<BountyMission, MissionController.MissionsCursor> findBountyMissionList(BountyMissionSearchCondition bountyMissionSearchCondition, PagingRequest<MissionController.MissionsCursor> pageable);
}
