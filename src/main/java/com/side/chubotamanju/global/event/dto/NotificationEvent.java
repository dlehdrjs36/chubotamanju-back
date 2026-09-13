package com.side.chubotamanju.global.event.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "eventName" // JSON 바디에서 종류를 구분할 기준 필드명
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MissionSubmissionRegisterEvent.class, name = "missionsSubmissionsRegister")
        , @JsonSubTypes.Type(value = MissionSubmissionRejectReviewEvent.class, name = "missionSubmissionRejectReview")
        , @JsonSubTypes.Type(value = MissionSubmissionApproveReviewEvent.class, name = "missionSubmissionApproveReview")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class NotificationEvent {

    private List<String> receiverDiscordIdList;

    // 알림 받을 대상자 설정
    protected NotificationEvent(List<String> receiverDiscordIdList) {
        this.receiverDiscordIdList = receiverDiscordIdList;
    }

    // Class<T>의 범위를 오직 NotificationEvent의 자식 클래스로만 강제 제한하여 형변환
    public <T extends NotificationEvent> T unwrap(Class<? extends NotificationEvent> targetClass) {
        // 런타임에 실제 객체의 타입이 인자로 받은 타입과 일치하는지 안전하게 검증
        if (targetClass.isInstance(this)) {
            return (T) this; // Jackson이 채워둔 진짜 자식 인스턴스(this)를 반환
        }
        throw new IllegalArgumentException("요청하신 클래스 타입과 실제 객체의 타입이 일치하지 않습니다.");
    }
}
