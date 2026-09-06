package com.aryan.razorpay.common_lib.audit;

import com.aryan.razorpay.common_lib.context.MerchantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
public class AuditAwareImpl implements AuditorAware<String> {

    private final MerchantContext merchantContext;

    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            String keyId = merchantContext.getKeyId();

            if (keyId != null && !keyId.isBlank())
                return Optional.of(keyId);

            if (merchantContext.getMerchantId() != null)
                return Optional.of("merchant_id: "+merchantContext.getMerchantId());
        } catch (Exception _) {

        }

        return Optional.of("SYSTEM");
    }
}
