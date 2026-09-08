package com.aryan.razorpay.operations_service.repositories;

import com.aryan.razorpay.common_lib.enums.SettlementStatus;
import com.aryan.razorpay.operations_service.entities.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SettlementRepository extends JpaRepository<Settlement, UUID> {
    List<Settlement> findByStatus(SettlementStatus settlementStatus);
}
