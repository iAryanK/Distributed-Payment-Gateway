package com.aryan.razorpay.payment_service.config;

import com.aryan.razorpay.common_lib.enums.PaymentMethod;
import com.aryan.razorpay.payment_service.processor.PaymentProcessor;
import com.aryan.razorpay.payment_service.processor.strategy.CardPaymentProcessor;
import com.aryan.razorpay.payment_service.processor.strategy.NetBankingPaymentProcessor;
import com.aryan.razorpay.payment_service.processor.strategy.UpiPaymentProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentProcessorConfig {

    private final NetBankingPaymentProcessor netBankingPaymentProcessor;
    private final UpiPaymentProcessor upiPaymentProcessor;
    private final CardPaymentProcessor cardPaymentProcessor;

    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentProcessorMap() {
        return Map.of(
                PaymentMethod.CARD, cardPaymentProcessor,
                PaymentMethod.UPI, upiPaymentProcessor,
                PaymentMethod.NETBANKING, netBankingPaymentProcessor
        );
    }
}
