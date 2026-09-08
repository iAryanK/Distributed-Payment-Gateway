package com.aryan.razorpay.merchant_service.repositories;

import com.aryan.razorpay.merchant_service.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByEmail(String email);
}
