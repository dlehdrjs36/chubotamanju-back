package com.side.chubotamanju.domain.user.repository;


import com.side.chubotamanju.domain.user.entity.BountyMissionFile;
import com.side.chubotamanju.domain.user.entity.BountyMissionSubmission;
import com.side.chubotamanju.domain.user.enums.BountySubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BountyMissionFileRepository extends JpaRepository<BountyMissionFile, Long> {

}
