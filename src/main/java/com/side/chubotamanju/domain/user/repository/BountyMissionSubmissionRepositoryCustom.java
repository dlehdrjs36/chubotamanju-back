package com.side.chubotamanju.domain.user.repository;

import com.side.chubotamanju.domain.bounty.controller.dto.api.BountyHunterSubmissionDto;
import com.side.chubotamanju.domain.user.entity.BountyMissionSubmission;
import com.side.chubotamanju.domain.user.repository.dto.BountyMissionSearchCondition;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionSubmissionServiceDto;
import com.side.chubotamanju.global.common.PagingRequest;
import com.side.chubotamanju.global.common.PagingResponse;

public interface BountyMissionSubmissionRepositoryCustom {
    PagingResponse<BountyMissionSubmission> findBountyMissionSubmissionList(String guildId, String hunterDiscordUserId, PagingRequest pagingRequest);
    PagingResponse<BountyHunterSubmissionDto> findBountyMissionSubmissionListView(String guildId, String hunterDiscordUserId, PagingRequest pagingRequest);
}
