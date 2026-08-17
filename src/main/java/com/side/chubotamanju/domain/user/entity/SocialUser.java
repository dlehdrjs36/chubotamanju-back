package com.side.chubotamanju.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

@Entity
@Table(
    name = "social_users",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_social_users_provider_provider_id",
            columnNames = {"provider", "provider_id"}
        )
    }
)
@Getter
@Builder
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자 생성 (Builder가 사용)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 생성 (JPA 등이 사용)
public class SocialUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_users_id")
    private Long socialUsersId;

    @Column(name = "provider", nullable = false, length = 50)
    private String provider;

    @Column(name = "provider_id", nullable = false, length = 255)
    private String providerId;

    @Column(name = "provider_username", length = 255)
    private String providerUsername;

    @Column(name = "provider_global_name", length = 255)
    private String providerGlobalName;

    @Column(name = "provider_email", length = 255)
    private String providerEmail;

    @Column(name = "provider_verified", nullable = false)
    private Boolean providerVerified;

}
