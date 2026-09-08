package com.aryan.razorpay.operations_service.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "settlement_payment")
public class SettlementPayment {

    @EmbeddedId
    private SettlementPaymentId id;

    @MapsId("settlementId")   // map this field to settlement_id field of composite key
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "settlement_id", nullable = false)
    private Settlement settlement;
}
