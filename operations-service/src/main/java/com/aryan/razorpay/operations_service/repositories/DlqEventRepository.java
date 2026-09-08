package com.aryan.razorpay.operations_service.repositories;

import com.aryan.razorpay.operations_service.entities.DlqEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DlqEventRepository extends JpaRepository<DlqEvent, UUID> {
}
