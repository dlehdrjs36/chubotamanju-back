package com.side.chubotamanju.domain.user.service;

import com.side.chubotamanju.domain.user.exception.UserNotFoundException;
import com.side.chubotamanju.global.exception.enums.GlobalErrorCode;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DiscordLookupService {

    private final static Map<String, Object> DISCORD_CACHE_MAP = new ConcurrentHashMap<>();
    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://discord.com/api")
            .defaultHeader("Authorization", "Bot")
            .build();

    public Map<String, Object> getDiscordUser(String userId) {
        Map<String, Object> discordUser = (Map<String, Object>) DISCORD_CACHE_MAP.getOrDefault(userId, new HashMap<>());

        if("SYSTEM".equals(userId)) return discordUser;

        if (discordUser.size() == 0) {
            Map<String, Object> body = restClient.get()
                    .uri("/users/{userId}", userId)
                    .retrieve()
                    // 💡 4xx, 5xx 에러가 발생하면 여기서 바로 가로채서 커스텀 예외를 던집니다.
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        int statusCode = response.getStatusCode().value();
                        // 필요하다면 response.getBody()를 파싱하여 디스코드측 에러 메시지를 읽을 수도 있습니다.
                        System.out.printf("error userID : " + userId);
                        throw new UserNotFoundException(GlobalErrorCode.USER_NOT_FOUND);
                    })
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            DISCORD_CACHE_MAP.put(userId, body);
            return (Map<String, Object>) DISCORD_CACHE_MAP.get(userId);
        }

        return discordUser;

    }

    public Map<String, Object> getDiscordGuild(String guildId) {
        Map<String, Object> discordGuild = (Map<String, Object>) DISCORD_CACHE_MAP.getOrDefault(guildId, new HashMap<>());
        if (discordGuild.size() == 0) {
            Map<String, Object> body = restClient.get()
                    .uri("/guilds/{guildId}", guildId)
                    .retrieve()
                    // 💡 4xx, 5xx 에러가 발생하면 여기서 바로 가로채서 커스텀 예외를 던집니다.
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        int statusCode = response.getStatusCode().value();
                        // 필요하다면 response.getBody()를 파싱하여 디스코드측 에러 메시지를 읽을 수도 있습니다.
                        throw new UserNotFoundException(GlobalErrorCode.USER_NOT_FOUND);
                    })
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            DISCORD_CACHE_MAP.put(guildId, body);
            return (Map<String, Object>) DISCORD_CACHE_MAP.get(guildId);
        }

        return discordGuild;

    }

}
