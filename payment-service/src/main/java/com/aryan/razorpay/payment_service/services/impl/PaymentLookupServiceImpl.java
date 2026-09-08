package com.aryan.razorpay.payment_service.services.impl;

import com.aryan.razorpay.common_lib.dto.PaymentSettlementView;
import com.aryan.razorpay.common_lib.enums.PaymentStatus;
import com.aryan.razorpay.payment_service.api.PaymentLookupService;
import com.aryan.razorpay.payment_service.entities.Payment;
import com.aryan.razorpay.payment_service.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentLookupServiceImpl implements PaymentLookupService {

    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public List<PaymentSettlementView> findUnsettledCapturedPayments(UUID merchantId) {
        List<Payment> payments = paymentRepository.findByMerchantIdAndStatusForUpdate(merchantId, PaymentStatus.CAPTURED);

        return payments.stream()
                .map(p -> new PaymentSettlementView(
                        p.getId(),
                        p.getAmount().getAmountUnits(),
                        0, // TODO: replace with actual refund values from RefundRepository
                        p.getAmount().getCurrency()))
                .toList();
    }

    @Override
    @Transactional
    public void markSettled(List<UUID> paymentList) {
        LocalDateTime now = LocalDateTime.now();
        List<Payment> payments = paymentRepository.findAllById(paymentList);
        for (Payment payment : payments) {
            payment.setStatus(PaymentStatus.SETTLED);
            payment.setSettledAt(now);
        }
        paymentRepository.saveAll(payments);
    }
}
