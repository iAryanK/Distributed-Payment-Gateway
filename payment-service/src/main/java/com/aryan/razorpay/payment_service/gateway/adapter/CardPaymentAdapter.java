package com.aryan.razorpay.payment_service.gateway.adapter;

import com.aryan.razorpay.common_lib.dto.PaymentProcessorResponse;
import com.aryan.razorpay.common_lib.dto.VaultChargeRequest;
import com.aryan.razorpay.payment_service.client.VaultServiceClient;
import com.aryan.razorpay.payment_service.gateway.PaymentAdapter;
import com.aryan.razorpay.payment_service.gateway.dto.PaymentRequest;
import com.aryan.razorpay.payment_service.gateway.dto.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CardPaymentAdapter implements PaymentAdapter {

    private final VaultServiceClient vaultServiceClient;

    @Override
    public PaymentResult initiate(PaymentRequest request) {
        String token = (String) request.methodDetails().get("token");

        PaymentProcessorResponse response = vaultServiceClient.charge(
                new VaultChargeRequest(request.paymentId(), token, request.amount(), request.methodDetails())
        );

        return switch(response) {
            case PaymentProcessorResponse.Success success -> new PaymentResult.Success(success.bankReference());
            case PaymentProcessorResponse.Failure failure -> new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
            case PaymentProcessorResponse.Pending pending -> new PaymentResult.Pending(pending.processorReference());
        };
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("CARD_REF");
    }
}
