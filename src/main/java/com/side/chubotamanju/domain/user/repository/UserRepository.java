package com.side.chubotamanju.domain.user.repository;


import com.side.chubotamanju.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByDiscordUserId(String discordUserId);
    List<User> findByGuildId(String guildId);
    Optional<User> findByGuildIdAndDiscordUserId(String guildId, String discordUserId);

    long countByGuildIdAndDiscordUserIdNot(String guildId, String discordUserId);

    @Query("""
            SELECT COUNT(u) FROM User u
            WHERE u.guildId = :guildId
              AND u.discordUserId <> :excludedDiscordUserId
              AND u.rating IS NOT NULL
              AND u.rating <= :maxRating
            """)
    long countEligibleUsersByRatingLimit(
            @Param("guildId") String guildId,
            @Param("excludedDiscordUserId") String excludedDiscordUserId,
            @Param("maxRating") java.math.BigDecimal maxRating
    );
}
