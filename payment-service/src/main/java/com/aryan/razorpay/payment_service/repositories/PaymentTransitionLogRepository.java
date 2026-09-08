package com.aryan.razorpay.payment_service.repositories;

import com.aryan.razorpay.payment_service.entities.PaymentTransitionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentTransitionLogRepository extends JpaRepository<PaymentTransitionLog, UUID> {
}
