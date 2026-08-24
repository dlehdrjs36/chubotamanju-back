package com.side.chubotamanju.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "upload_tokens")
@Getter @Setter
public class UploadToken {

    @Id
    @Column(length = 64)
    private String token;

    @Column(name = "guild_id", nullable = false, length = 50)
    private String guildId;

    @Column(name = "discord_user_id", nullable = false, length = 50)
    private String discordUserId;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
