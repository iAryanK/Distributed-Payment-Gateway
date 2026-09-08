package com.aryan.razorpay.operations_service.repositories;

import com.aryan.razorpay.common_lib.enums.OutboxStatus;
import com.aryan.razorpay.operations_service.entities.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus outboxStatus);
}
