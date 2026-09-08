package com.aryan.razorpay.operations_service.entities;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SettlementPaymentId {

    private UUID settlementId;

    private UUID paymentId;
}
