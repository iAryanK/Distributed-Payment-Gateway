package com.aryan.razorpay.vault_service.processor;

import com.aryan.razorpay.common_lib.dto.PaymentProcessorRequest;
import com.aryan.razorpay.common_lib.dto.PaymentProcessorResponse;
import com.aryan.razorpay.common_lib.util.RandomizedUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CardPaymentProcessor {

    public static final String PAN_CARD_DECLINED = "400000000002";
    public static final String PAN_CARD_EXPIRED = "400000000069";

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
