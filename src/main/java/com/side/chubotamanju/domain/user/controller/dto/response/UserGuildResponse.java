package com.side.chubotamanju.domain.user.controller.dto.response;

import com.side.chubotamanju.domain.user.service.dto.UserServiceDto;
import lombok.Data;

@Data
public class UserGuildResponse {
    private String guildId;
    private String guildName;
    private String guildIcon;

    public static UserGuildResponse convert(UserServiceDto userServiceDto) {
        UserGuildResponse userGuildResponse = new UserGuildResponse();
        userGuildResponse.setGuildId(userServiceDto.getGuildId());

        return userGuildResponse;
    }

}
