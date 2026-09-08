package com.aryan.razorpay.merchant_service.repositories;

import com.aryan.razorpay.common_lib.enums.MerchantStatus;
import com.aryan.razorpay.merchant_service.entities.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MerchantRepository extends JpaRepository<Merchant, UUID> {
    boolean existsByEmail(String email);

    List<Merchant> findByStatus(MerchantStatus merchantStatus);
}
