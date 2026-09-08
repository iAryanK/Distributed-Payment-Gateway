package com.aryan.razorpay.vault_service.repository;

import com.aryan.razorpay.vault_service.entities.VaultCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VaultCardRepository extends JpaRepository<VaultCard, UUID> {
}
