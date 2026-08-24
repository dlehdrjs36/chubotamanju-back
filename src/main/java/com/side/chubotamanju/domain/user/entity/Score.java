package com.side.chubotamanju.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "scores")
@Getter
@Setter
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
        어떤 유저 데이터인지 연결
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    /*
        JSON 원본 그대로 저장 ⭐⭐⭐⭐⭐
     */
    @Column(columnDefinition = "json")
    private String rawData;

    /*
        조회 최적화용 메타 (강력 추천 😏)
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal rating;
    private Integer level;

    private OffsetDateTime updatedAt;
}
