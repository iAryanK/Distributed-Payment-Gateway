package com.aryan.razorpay.merchant_service.repositories;

import com.aryan.razorpay.merchant_service.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByMerchant_IdAndEmail(UUID merchantId, String email);
}
