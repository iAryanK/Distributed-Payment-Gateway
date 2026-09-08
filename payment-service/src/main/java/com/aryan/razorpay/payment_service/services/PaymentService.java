package com.aryan.razorpay.payment_service.services;

import com.aryan.razorpay.payment_service.dto.request.PaymentInitRequest;
import com.aryan.razorpay.payment_service.dto.response.PaymentResponse;
import jakarta.validation.Valid;

import java.util.UUID;

public interface PaymentService {
    PaymentResponse initiate(UUID merchantId, @Valid PaymentInitRequest request);

    PaymentResponse capture(UUID merchantId, UUID paymentId);

    void resolveAuthorization(UUID paymentId, boolean approve, String bankRef, String errorCode, String errorDescription);
}
