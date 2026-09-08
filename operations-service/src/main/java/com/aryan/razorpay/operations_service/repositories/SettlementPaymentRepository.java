package com.aryan.razorpay.operations_service.repositories;

import com.aryan.razorpay.operations_service.entities.Settlement;
import com.aryan.razorpay.operations_service.entities.SettlementPayment;
import com.aryan.razorpay.operations_service.entities.SettlementPaymentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementPaymentRepository extends JpaRepository<SettlementPayment, SettlementPaymentId> {
    List<SettlementPayment> findBySettlement(Settlement settlement);
}
