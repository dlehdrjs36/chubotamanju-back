package com.side.chubotamanju.domain.user.repository;


import com.side.chubotamanju.domain.user.entity.BountyHunterProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BountyHunterProfileRepository extends JpaRepository<BountyHunterProfile, Long> {
    List<BountyHunterProfile> findByGuildId(String guildId);
    Optional<BountyHunterProfile> findByGuildIdAndDiscordUserId(String guildId, String discordUserId);
}
