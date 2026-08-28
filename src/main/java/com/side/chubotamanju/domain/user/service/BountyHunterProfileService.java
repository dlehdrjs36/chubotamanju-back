package com.side.chubotamanju.domain.user.service;

import com.side.chubotamanju.domain.user.entity.BountyHunterProfile;
import com.side.chubotamanju.domain.user.repository.BountyHunterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BountyHunterProfileService {
    private final BountyHunterProfileRepository bountyHunterProfileRepository;

    public List<String> findGuildIdList(String discordUserId) {
        return bountyHunterProfileRepository.findByDiscordUserId(discordUserId)
                .stream()
                .map(BountyHunterProfile::getGuildId)
                .toList();
    }
}
