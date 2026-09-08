package com.aryan.razorpay.payment_service.entities;

import com.aryan.razorpay.common_lib.entities.BaseEntity;
import com.aryan.razorpay.common_lib.entities.Money;
import com.aryan.razorpay.common_lib.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_record",
        indexes = {
                @Index(name = "idx_order_merchant_id", columnList = "merchant_id"),
                @Index(name = "idx_order_id_merchant_id", columnList = "id, merchant_id")
        })
public class OrderRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // NO FK - cross-service boundary
    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(name = "customer_id")
    private UUID customerId;

    // In java code, we will use amount, but internally it will use amountUnits and currency
    @Embedded
    private Money amount;

    private String receipt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.CREATED;

    @Column(nullable = false)
    @Builder.Default
    private Integer attempts = 0;

    @JdbcTypeCode((SqlTypes.JSON))
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> notes;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}
