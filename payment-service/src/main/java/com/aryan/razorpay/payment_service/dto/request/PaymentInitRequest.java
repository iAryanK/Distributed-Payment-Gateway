package com.aryan.razorpay.payment_service.dto.request;

import com.aryan.razorpay.common_lib.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record PaymentInitRequest(
        @NotNull(message = "order id is required")
        UUID orderId,

        @NotNull(message = "payment method is required")
        PaymentMethod method,

        Map<String, Object> methodDetails
) {
}
