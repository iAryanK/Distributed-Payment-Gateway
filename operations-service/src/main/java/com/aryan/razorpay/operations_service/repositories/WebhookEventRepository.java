package com.aryan.razorpay.operations_service.repositories;

import com.aryan.razorpay.common_lib.enums.WebhookEventStatus;
import com.aryan.razorpay.operations_service.entities.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, UUID> {
    List<WebhookEvent> findByStatusAndNextRetryAtBefore(WebhookEventStatus webhookEventStatus, LocalDateTime now);
}
