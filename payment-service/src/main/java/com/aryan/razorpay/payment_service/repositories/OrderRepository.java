package com.aryan.razorpay.payment_service.repositories;

import com.aryan.razorpay.payment_service.entities.OrderRecord;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderRecord, UUID> {

    boolean existsByMerchantIdAndReceipt(UUID merchantId, String receipt);

    Optional<OrderRecord> findByIdAndMerchantId(UUID orderId, UUID merchantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT o FROM OrderRecord o
            WHERE o.id = :uuid AND o.merchantId = :merchantId
            """)
    Optional<OrderRecord> findByIdAndMerchantIdForUpdate(UUID uuid, UUID merchantId);
}
