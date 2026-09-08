package com.aryan.razorpay.operations_service.outbox;

import com.aryan.razorpay.common_lib.config.KafkaProperties;
import com.aryan.razorpay.common_lib.enums.OutboxStatus;
import com.aryan.razorpay.operations_service.entities.OutboxEvent;
import com.aryan.razorpay.operations_service.repositories.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPoller {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final OutboxResultHandler outboxResultHandler;

    @Scheduled(fixedDelay = 5000)
    public void poll() {
        List<OutboxEvent> pendingEvents = outboxEventRepository
                .findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        for (OutboxEvent event : pendingEvents) {
            try {
                String topic = kafkaProperties.topicFor(event.getAggregateType());
                String key = extractMerchantId(event.getPayload());
                Map<String, Object> envelope = Map.of(
                        "eventType", event.getEventType(),
                        "aggregateType", event.getAggregateType().name(),
                        "aggregateId", event.getAggregateId().toString(),
                        "data", event.getPayload()
                );

                kafkaTemplate.send(topic, key, envelope)
                        .get(5, TimeUnit.SECONDS);

                outboxResultHandler.handleEventPublished(event);
            } catch (Exception e) {
                log.error("Outbox event failed, eventId: {}, attempts: {}", event.getId(), event.getAttempts());
                outboxResultHandler.handleEventFailed(event, e.getMessage());
            }
        }
    }

    private String extractMerchantId(Map<String, Object> payload) {
        Object merchantId = payload.get("merchantId");
        return merchantId != null ? merchantId.toString() : "unknown";
    }
}
