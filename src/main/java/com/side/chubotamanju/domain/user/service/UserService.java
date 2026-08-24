package com.side.chubotamanju.domain.user.service;

import com.side.chubotamanju.domain.user.entity.User;
import com.side.chubotamanju.domain.user.repository.UserRepository;
import com.side.chubotamanju.domain.user.service.dto.UserServiceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserServiceDto> findByDiscordUserId(String discordUserId) {
        List<UserServiceDto> userList = userRepository.findByDiscordUserId(discordUserId).stream()
                .map(user -> UserServiceDto.builder()
                        .guildId(user.getGuildId())
                        .build())
                .toList();

        return userList;
    }
}
