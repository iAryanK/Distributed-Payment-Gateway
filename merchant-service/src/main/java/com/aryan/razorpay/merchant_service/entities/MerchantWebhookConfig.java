package com.aryan.razorpay.merchant_service.entities;

import com.aryan.razorpay.common_lib.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "merchant_webhook_config",
        indexes = {
                @Index(name = "idx_webhook_merchant_id", columnList = "merchant_id, enabled")
        })
public class MerchantWebhookConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false, length = 500)
    private String targetUrl;

    @Column(length = 255)
    private String webhookSecret;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(length = 255)
    private String eventTypes;

    public boolean isSubscribedTo(String eventType) {
        if (eventTypes == null || eventTypes.isBlank()) {
            return true;
        }
        for (String type : eventTypes.split(",")) {
            String trimmed = type.trim();
            if (trimmed.equalsIgnoreCase("ALL") || trimmed.equalsIgnoreCase(eventType)) {
                return true;
            }
        }
        return false;
    }
}
