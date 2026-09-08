package com.aryan.razorpay.payment_service.services.statemachine;

import com.aryan.razorpay.common_lib.enums.PaymentActor;
import com.aryan.razorpay.common_lib.enums.PaymentEvent;
import com.aryan.razorpay.common_lib.enums.PaymentStatus;
import com.aryan.razorpay.payment_service.entities.Payment;
import com.aryan.razorpay.payment_service.entities.PaymentTransitionLog;
import com.aryan.razorpay.payment_service.repositories.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {

    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStateMachine paymentStateMachine;

    public PaymentStatus apply(Payment payment, PaymentEvent event) {
        PaymentStatus next = paymentStateMachine.transition(payment.getStatus(), event);

        PaymentTransitionLog log = PaymentTransitionLog.builder()
                .payment(payment)
                .fromStatus(payment.getStatus())
                .event(event)
                .toStatus(next)
                .actor(PaymentActor.SYSTEM)    // TODO: fetch merchant context to identify actor
                .occurredAt(LocalDateTime.now())
                .build();

        payment.setStatus(next);
        paymentTransitionLogRepository.save(log);

        return next;
    }
}
