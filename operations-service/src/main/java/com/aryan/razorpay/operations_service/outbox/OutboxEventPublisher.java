package com.aryan.razorpay.operations_service.outbox;

import com.aryan.razorpay.common_lib.enums.EventAggregateType;
import com.aryan.razorpay.operations_service.entities.OutboxEvent;
import com.aryan.razorpay.operations_service.repositories.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;

    public void publish(EventAggregateType aggregateType, UUID aggregateId, String eventType, Map<String, Object> payload) {
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(payload)
                .build();

        outboxEventRepository.save(outboxEvent);
    }
}
