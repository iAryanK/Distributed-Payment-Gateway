package com.aryan.razorpay.payment_service.services.statemachine;

import com.aryan.razorpay.common_lib.context.MerchantContext;
import com.aryan.razorpay.common_lib.enums.PaymentActor;
import com.aryan.razorpay.common_lib.enums.PaymentEvent;
import com.aryan.razorpay.common_lib.enums.PaymentStatus;
import com.aryan.razorpay.payment_service.entities.Payment;
import com.aryan.razorpay.payment_service.entities.PaymentTransitionLog;
import com.aryan.razorpay.payment_service.repositories.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {

    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStateMachine paymentStateMachine;
    private final MerchantContext merchantContext;

    public PaymentStatus apply(Payment payment, PaymentEvent event) {
        PaymentStatus next = paymentStateMachine.transition(payment.getStatus(), event);
        PaymentActor actor = getPaymentActor();

        PaymentTransitionLog log = PaymentTransitionLog.builder()
                .payment(payment)
                .fromStatus(payment.getStatus())
                .event(event)
                .toStatus(next)
                .actor(actor)
                .occurredAt(LocalDateTime.now())
                .build();

        payment.setStatus(next);
        paymentTransitionLogRepository.save(log);

        return next;
    }

    private PaymentActor getPaymentActor() {
        try {
            String keyId = merchantContext.getKeyId();
            UUID merchantId = merchantContext.getMerchantId();

            if (keyId != null && !keyId.isBlank())
                return PaymentActor.CUSTOMER;
            else if (merchantId != null)
                return PaymentActor.MERCHANT;
        } catch (Exception _) {
        }

        return PaymentActor.SYSTEM;
    }
}
