package com.side.chubotamanju.domain.user.repository;

import com.side.chubotamanju.domain.bounty.controller.dto.api.BountyPendingSubmissionDto;
import com.side.chubotamanju.domain.user.entity.BountyMissionSubmission;
import com.side.chubotamanju.domain.user.service.BountyMissionSubmissionService;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionSubmissionQueryDto;
import com.side.chubotamanju.global.common.PagingRequest;
import com.side.chubotamanju.global.common.PagingResponse;

public interface BountyMissionSubmissionRepositoryCustom {
    PagingResponse<BountyMissionSubmission> findBountyMissionSubmissionList(String guildId, String hunterDiscordUserId, PagingRequest pagingRequest);
    PagingResponse<BountyMissionSubmissionQueryDto> findBountyMissionSubmissionListView(BountyMissionSubmissionService.BountyMissionSubmissionSearchCondition searchCondition, PagingRequest pagingRequest);
    PagingResponse<BountyPendingSubmissionDto> findBountyPendingSubmissionDtoView(String guildId, String hunterDiscordUserId, PagingRequest pagingRequest);
}
