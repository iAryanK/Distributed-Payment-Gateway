package com.aryan.razorpay.vault_service.services;

import com.aryan.razorpay.common_lib.dto.PaymentProcessorResponse;
import com.aryan.razorpay.common_lib.entities.Money;
import com.aryan.razorpay.vault_service.dto.request.TokenizeRequest;
import com.aryan.razorpay.vault_service.dto.response.TokenizeResponse;

import java.util.Map;
import java.util.UUID;

public interface VaultService {
    TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId);

    PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails);
}
