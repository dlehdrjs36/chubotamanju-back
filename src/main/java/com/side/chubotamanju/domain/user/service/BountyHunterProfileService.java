package com.side.chubotamanju.domain.user.service;

import com.side.chubotamanju.domain.user.entity.BountyHunterProfile;
import com.side.chubotamanju.domain.user.repository.BountyHunterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BountyHunterProfileService {
    private final BountyHunterProfileRepository bountyHunterProfileRepository;

    public List<String> findGuildIdList(String discordUserId) {
        return bountyHunterProfileRepository.findByDiscordUserId(discordUserId)
                .stream()
                .map(BountyHunterProfile::getGuildId)
                .toList();
    }

    /**
     * 길드에 있는 헌터 정보 조회
     * @param guildId
     * @param discordUserId
     * @return
     */
    public BountyHunterProfile findBountyHunterProfileBy(String guildId, String discordUserId) {
        return bountyHunterProfileRepository.findByGuildIdAndDiscordUserId(guildId, discordUserId)
                .orElseThrow(() -> new IllegalArgumentException("해당 헌터 정보를 찾을 수 없어."));
    }

}
