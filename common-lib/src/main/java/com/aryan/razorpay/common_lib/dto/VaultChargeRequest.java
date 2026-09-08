package com.aryan.razorpay.common_lib.dto;

import com.aryan.razorpay.common_lib.entities.Money;

import java.util.Map;
import java.util.UUID;

public record VaultChargeRequest(
        UUID paymentId,
        String token,
        Money amount,
        Map<String, Object> methodDetails
) {
}
