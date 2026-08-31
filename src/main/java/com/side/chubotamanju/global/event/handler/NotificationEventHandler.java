package com.side.chubotamanju.global.event.handler;

import com.side.chubotamanju.global.event.dto.MissionSubmissionRegisterEvent;
import com.side.chubotamanju.global.event.dto.NotificationEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestClient;

@Component
public class NotificationEventHandler {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("http://localhost:9030/api/bot")
            .defaultHeader("X-ChubotaBack", "chubotamanjuside")
            .build();

    @Async("taskExecutor")
    @TransactionalEventListener
    public void handleMissionSubmissionRegisterEvent(NotificationEvent notificationEvent) {
        restClient.post()
                .uri("/missions/submissions/complete")
                .body(notificationEvent)
                .retrieve()
                .toBodilessEntity();
    }
}
