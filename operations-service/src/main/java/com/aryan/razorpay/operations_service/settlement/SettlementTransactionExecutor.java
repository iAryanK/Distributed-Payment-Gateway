package com.aryan.razorpay.operations_service.settlement;

import com.aryan.razorpay.common_lib.dto.PaymentSettlementView;
import com.aryan.razorpay.common_lib.dto.SettlementBankDetails;
import com.aryan.razorpay.common_lib.entities.Money;
import com.aryan.razorpay.common_lib.enums.EventAggregateType;
import com.aryan.razorpay.common_lib.enums.SettlementStatus;
import com.aryan.razorpay.common_lib.exceptions.ResourceNotFoundException;
import com.aryan.razorpay.operations_service.client.MerchantServiceClient;
import com.aryan.razorpay.operations_service.client.PaymentServiceClient;
import com.aryan.razorpay.operations_service.entities.Settlement;
import com.aryan.razorpay.operations_service.entities.SettlementPayment;
import com.aryan.razorpay.operations_service.entities.SettlementPaymentId;
import com.aryan.razorpay.operations_service.outbox.OutboxEventPublisher;
import com.aryan.razorpay.operations_service.repositories.SettlementPaymentRepository;
import com.aryan.razorpay.operations_service.repositories.SettlementRepository;
import com.aryan.razorpay.operations_service.settlement.dto.BankTransferResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementTransactionExecutor {
    private final SettlementRepository settlementRepository;
    private final SettlementPaymentRepository settlementPaymentRepository;
    private final BankTransferProcessor bankTransferProcessor;
    private final OutboxEventPublisher outboxEventPublisher;
    private final MerchantServiceClient merchantServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    private static final double FEE_RATE = 0.02;
    private static final double GST_RATE = 0.18;

    @Transactional
    public void processForMerchant(UUID merchantId, LocalDate settlementDate) {
        List<PaymentSettlementView> unsettledPayments = paymentServiceClient.findUnsettledCapturedPayments(merchantId);
        if (unsettledPayments.isEmpty()) return;

        log.info("Processing {} unsettled payments for merchantID {} on {}",
                unsettledPayments.size(), merchantId, settlementDate);

        Integer grossAmount = unsettledPayments.stream()
                .map(PaymentSettlementView::amountUnits)
                .reduce(Integer::sum)
                .orElse(0);
        Money gross = Money.of(grossAmount, unsettledPayments.getFirst().currency());

        int fee = Math.toIntExact(Math.round(gross.getAmountUnits() * FEE_RATE));
        int gst = Math.toIntExact(Math.round(fee * GST_RATE));
        Money feeAmount = Money.of(fee, gross.getCurrency());
        Money gstAmount = Money.of(gst, gross.getCurrency());
        Money netAmount = gross.subtract(feeAmount).subtract(gstAmount);

        Settlement settlement = Settlement.builder()
                .merchantId(merchantId)
                .grossAmount(gross)
                .feeAmount(feeAmount)
                .gstAmount(gstAmount)
                .netAmount(netAmount)
                .status(SettlementStatus.INITIATED)
                .build();

        settlementRepository.save(settlement);

        try {
            List<SettlementPayment> links = new ArrayList<>();
            for (PaymentSettlementView p : unsettledPayments) {
                links.add(SettlementPayment.builder()
                        .id(new SettlementPaymentId(settlement.getId(), p.paymentId()))
                        .settlement(settlement)
                        .build());
            }
            settlementPaymentRepository.saveAll(links);

            SettlementBankDetails settlementBankDetails = merchantServiceClient.getSettlementBankDetails(merchantId);

            // call the bank transfer service to transfer net amount to merchant settlement bank details
            BankTransferResult bankTransferResult = bankTransferProcessor.initiate(settlement.getId(), merchantId, netAmount,
                    settlementBankDetails.accountNumber(), settlementBankDetails.ifsc());

            settlement.setStatus(SettlementStatus.TRANSFER_PENDING);
            settlement.setBankReference(bankTransferResult.registrationRef());

            settlementRepository.save(settlement);
        } catch (Exception e) {
            log.error("Settlement failed for settlementId: {} on date: {}", settlement.getId(), settlementDate, e);
            settlement.setStatus(SettlementStatus.FAILED);
            settlementRepository.save(settlement);
        }
    }

    @Transactional
    public void resolveTransfer(UUID settlementId, String errorCode, String errorDescription) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new ResourceNotFoundException("Settlement", settlementId));

        if (settlement.getStatus() != SettlementStatus.TRANSFER_PENDING) {
            log.info("Settlement resolved, skipping for id: {}", settlement.getId());
            return;
        }

        if (errorCode == null) {
            settlement.setStatus(SettlementStatus.PROCESSED);
            settlement.setProcessedAt(LocalDateTime.now());
            settlementRepository.save(settlement);

            List<SettlementPayment> settlementPayments = settlementPaymentRepository.findBySettlement(settlement);
            List<UUID> paymentIds = settlementPayments.stream().map(SettlementPayment::getId)
                            .map(SettlementPaymentId::getPaymentId)
                            .toList();
            paymentServiceClient.markSettled(paymentIds);

            log.info("Settlement processed successfully, settlementId: {}", settlement.getId());
            outboxEventPublisher.publish(EventAggregateType.SETTLEMENT, settlementId,
                    "SETTLEMENT_PROCESSED", Map.of(
                            "settlementId", settlement,
                            "merchantId", settlement.getMerchantId(),
                            "status", settlement.getStatus().name(),
                            "settlementAmount", settlement.getNetAmount().getAmountUnits(),
                            "settlementCurrency", settlement.getNetAmount().getCurrency()
                    ));
        } else {
            settlement.setStatus(SettlementStatus.FAILED);
            settlement.setFailureReason(errorCode+" : "+errorDescription);
            settlementRepository.save(settlement);
            log.warn("Settlement failed, settlementId: {}", settlement.getId());
            outboxEventPublisher.publish(EventAggregateType.SETTLEMENT, settlementId,
                    "SETTLEMENT_FAILED", Map.of(
                            "settlementId", settlement,
                            "merchantId", settlement.getMerchantId(),
                            "status", settlement.getStatus().name(),
                            "settlementAmount", settlement.getNetAmount().getAmountUnits(),
                            "settlementCurrency", settlement.getNetAmount().getCurrency()
                    ));
        }

        settlementRepository.save(settlement);
    }
}
