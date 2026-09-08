package com.aryan.razorpay.payment_service.processor.strategy;

import com.aryan.razorpay.common_lib.dto.PaymentProcessorRequest;
import com.aryan.razorpay.common_lib.dto.PaymentProcessorResponse;
import com.aryan.razorpay.common_lib.util.RandomizedUtil;
import com.aryan.razorpay.payment_service.processor.PaymentProcessor;
import org.springframework.stereotype.Component;

@Component
public class NetBankingPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        final String BANK_CODE_FAIL = "BANK_CODE_FAIL";

        String bankCode = request.methodDetails() != null ? request.methodDetails().get("bank").toString() : null;

        if (BANK_CODE_FAIL.equals(bankCode))
            return new PaymentProcessorResponse.Failure("BANK_REJECTED", "Bank rejected the transaction registration");

        String processorRef = "NBK_PROCESSOR_"+ RandomizedUtil.randomBase64(16);

        return new PaymentProcessorResponse.Pending(processorRef);
    }
}
