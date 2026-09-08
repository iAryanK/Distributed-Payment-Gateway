package com.aryan.razorpay.merchant_service.cache;

import com.aryan.razorpay.common_lib.enums.Environment;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApiKeyCacheEntry(
    String keyId,

    UUID merchantId,

    String keySecretHash,

    String previousKeySecretHash,

    LocalDateTime gracePeriodExpiresAt,

    Environment environment,

    boolean enabled
) {

    public boolean isInGracePeriod() {
        return gracePeriodExpiresAt != null && LocalDateTime.now().isBefore(gracePeriodExpiresAt);
    }
}
