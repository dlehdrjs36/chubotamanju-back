package com.side.chubotamanju.global.event.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.side.chubotamanju.domain.user.service.dto.BountyMissionServiceDto;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "eventName" // JSON 바디에서 종류를 구분할 기준 필드명
)
@JsonSubTypes({
        // 새로운 알림 종류가 추가되면 여기에 추가
        @JsonSubTypes.Type(value = BountyMissionServiceDto.class, name = "missionsSubmissionsComplete")
})
public abstract class NotificationEvent {

    // 알림 받을 대상자
    public abstract List<String> getReceiverDiscordId();

    // 각 자식들이 자신만의 고유 속성으로 디스코드 메시지를 만들도록 추상 메서드 정의
    public abstract String makeMessage();

    // Class<T>의 범위를 오직 NotificationEvent의 자식 클래스로만 강제 제한하여 형변환
    @SuppressWarnings("unchecked")
    public <T extends NotificationEvent> T unwrap(Class<? extends NotificationEvent> targetClass) {
        // 런타임에 실제 객체의 타입이 인자로 받은 타입과 일치하는지 안전하게 검증
        if (targetClass.isInstance(this)) {
            return (T) this; // Jackson이 채워둔 진짜 자식 인스턴스(this)를 반환
        }
        throw new IllegalArgumentException("요청하신 클래스 타입과 실제 객체의 타입이 일치하지 않습니다.");
    }
}
