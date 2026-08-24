package com.side.chubotamanju.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "guild_notification_channel_settings")
@Getter
@Setter
public class GuildNotificationChannelSetting  extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guild_id", nullable = false, unique = true)
    private String guildId;

    @Column(name = "notification_channel_id", nullable = false)
    private String notificationChannelId;

}
