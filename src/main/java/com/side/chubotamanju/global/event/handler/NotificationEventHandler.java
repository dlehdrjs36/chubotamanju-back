package com.side.chubotamanju.global.event.handler;

import com.side.chubotamanju.global.event.dto.MissionSubmissionRegisterEvent;
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
    public void handleMissionSubmissionRegisterEvent(MissionSubmissionRegisterEvent missionSubmissionRegisterEvent) {
        restClient.post()
                .uri("/missions/submissions/complete")
                .body(missionSubmissionRegisterEvent.getBountyMissionServiceDto())
                .retrieve()
                .toBodilessEntity();
    }
}
