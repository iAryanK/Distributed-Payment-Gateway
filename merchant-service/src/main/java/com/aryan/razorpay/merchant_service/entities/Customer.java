package com.aryan.razorpay.merchant_service.entities;

import com.aryan.razorpay.common_lib.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "customer",
        indexes = {
                @Index(name = "idx_customer_merchant_id", columnList = "merchant_id"),
                @Index(name = "idx_customer_email", columnList = "email")
        })
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(length = 200)
    private String name;

    @Column(length = 200)
    private String email;

    @Column(length = 20)
    private String phone;

    private LocalDateTime deletedAt;
}
