package com.aryan.razorpay.payment_service.gateway.adapter;

import com.aryan.razorpay.common_lib.dto.PaymentProcessorRequest;
import com.aryan.razorpay.common_lib.dto.PaymentProcessorResponse;
import com.aryan.razorpay.common_lib.enums.PaymentMethod;
import com.aryan.razorpay.payment_service.gateway.PaymentAdapter;
import com.aryan.razorpay.payment_service.gateway.dto.PaymentRequest;
import com.aryan.razorpay.payment_service.gateway.dto.PaymentResult;
import com.aryan.razorpay.payment_service.processor.PaymentProcessorRouter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpiPaymentAdapter implements PaymentAdapter {

    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    public PaymentResult initiate(PaymentRequest request) {
        log.info("Initiate payment with upi adapter, paymentId: {}", request.paymentId());

        try {
            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.nonCard(
                    request.paymentId(),
                    PaymentMethod.UPI,
                    request.amount(),
                    request.methodDetails()
            );

            PaymentProcessorResponse paymentProcessorResponse = paymentProcessorRouter.charge(paymentProcessorRequest);

            return switch(paymentProcessorResponse) {
                case PaymentProcessorResponse.Failure failure -> new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
                case PaymentProcessorResponse.Pending pending -> new PaymentResult.Pending(pending.processorReference());
                case PaymentProcessorResponse.Success success -> new PaymentResult.Success(success.bankReference());
            };
        } catch (Exception e) {
            log.warn("upi failed, paymentId: {}", request.paymentId());
            return new PaymentResult.Failure("UPI_FAILURE", e.getMessage());
        }
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("UPI_REF");
    }
}
