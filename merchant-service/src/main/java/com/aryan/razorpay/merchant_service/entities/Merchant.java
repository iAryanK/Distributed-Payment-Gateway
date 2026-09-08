package com.aryan.razorpay.merchant_service.entities;

import com.aryan.razorpay.common_lib.entities.BaseEntity;
import com.aryan.razorpay.common_lib.enums.BusinessType;
import com.aryan.razorpay.common_lib.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "merchant",
        indexes = {
                @Index(name = "idx_merchant_status", columnList = "status")
        })
public class Merchant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 20)
    private String contactNumber;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @Column(length = 100)
    private String businessName;

    @Column(length = 200)
    private String websiteUrl;

    @Column(length = 100, nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MerchantStatus status = MerchantStatus.PENDING_KYC;

    @Column(length = 20)
    private String gstId;

    @Column(length = 20)
    private String panId;

    @Column(length = 200)
    private String settlementBankAccount;

    @Column(length = 20)
    private String settlementIfsc;

    @Column(length = 200)
    private String settlementBankAccountHolderName;
}
