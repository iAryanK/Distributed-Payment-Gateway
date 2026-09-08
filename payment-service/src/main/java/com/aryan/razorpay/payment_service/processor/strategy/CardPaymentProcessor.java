package com.aryan.razorpay.payment_service.processor.strategy;

import com.aryan.razorpay.common_lib.dto.PaymentProcessorRequest;
import com.aryan.razorpay.common_lib.dto.PaymentProcessorResponse;
import com.aryan.razorpay.common_lib.util.RandomizedUtil;
import com.aryan.razorpay.payment_service.processor.PaymentProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CardPaymentProcessor implements PaymentProcessor {

    public static final String PAN_CARD_DECLINED = "400000000002";
    public static final String PAN_CARD_EXPIRED = "400000000069";

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        String pan = request.pan();

        if (PAN_CARD_DECLINED.equals(pan)) {
            log.warn("Card declined");
            return new PaymentProcessorResponse.Failure("CARD_DECLINED", "Card declined by bank");
        }
        if (PAN_CARD_EXPIRED.equals(pan)) {
            log.warn("Card declined");
            return new PaymentProcessorResponse.Failure("CARD_EXPIRED", "Card has expired");
        }

        String processorRef = "CARD_PROCESSOR_"+ RandomizedUtil.randomBase64(16);

        return new PaymentProcessorResponse.Pending(processorRef);
    }
}
