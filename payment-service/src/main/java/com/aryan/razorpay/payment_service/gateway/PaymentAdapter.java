package com.aryan.razorpay.payment_service.gateway;

import com.aryan.razorpay.payment_service.gateway.dto.PaymentRequest;
import com.aryan.razorpay.payment_service.gateway.dto.PaymentResult;

import java.util.UUID;

public interface PaymentAdapter{
    PaymentResult initiate(PaymentRequest request);

    PaymentResult capture(UUID paymentId);
}
