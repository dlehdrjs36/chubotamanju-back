package com.side.chubotamanju.global.event.handler;

import com.side.chubotamanju.global.common.ChubotaHeader;
import com.side.chubotamanju.global.event.dto.NotificationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestClient;

@Component
public class NotificationEventHandler {

    private final RestClient restClient;

    public NotificationEventHandler(@Value("${app.api-key}") String expectedApiKey) {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:9030/api/bot")
                .defaultHeader(ChubotaHeader.API_KEY.getName(), expectedApiKey)
                .build();
    }

    @Async("taskExecutor")
    @TransactionalEventListener
    public void handleMissionSubmissionRegisterEvent(NotificationEvent notificationEvent) {
        restClient.post()
                .uri("/notification")
                .body(notificationEvent)
                .retrieve()
                .toBodilessEntity();
    }
}
