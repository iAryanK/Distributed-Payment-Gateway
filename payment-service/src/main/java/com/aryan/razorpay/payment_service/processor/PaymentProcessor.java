package com.aryan.razorpay.payment_service.processor;

import com.aryan.razorpay.common_lib.dto.PaymentProcessorRequest;
import com.aryan.razorpay.common_lib.dto.PaymentProcessorResponse;

public interface PaymentProcessor {

    PaymentProcessorResponse charge(PaymentProcessorRequest request);
}
