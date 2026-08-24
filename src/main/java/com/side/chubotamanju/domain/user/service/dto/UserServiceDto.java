package com.side.chubotamanju.domain.user.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserServiceDto {
    private Long id;

    private String guildId;

    private String discordUserId;

    private String name;

    private BigDecimal rating;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
