package com.aryan.razorpay.merchant_service.services.impl;

import com.aryan.razorpay.common_lib.dto.SettlementBankDetails;
import com.aryan.razorpay.common_lib.dto.WebhookTarget;
import com.aryan.razorpay.common_lib.enums.MerchantStatus;
import com.aryan.razorpay.common_lib.exceptions.ResourceNotFoundException;
import com.aryan.razorpay.merchant_service.api.MerchantLookupService;
import com.aryan.razorpay.merchant_service.entities.Merchant;
import com.aryan.razorpay.merchant_service.repositories.MerchantRepository;
import com.aryan.razorpay.merchant_service.repositories.WebhookConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantLookupServiceImpl implements MerchantLookupService {

    private final WebhookConfigRepository webhookConfigRepository;
    private final BytesEncryptor bytesEncryptor;
    private final MerchantRepository merchantRepository;

    @Override
    public List<WebhookTarget> getActiveConfigsForEvent(UUID merchantId, String eventType) {
        return webhookConfigRepository.findByMerchant_IdAndEnabledTrue(merchantId).stream()
                .filter(config -> config.isSubscribedTo(eventType))
                .map(config -> {
                    byte[] cipherBytes = Base64.getDecoder().decode(config.getWebhookSecret());
                    byte[] decryptedSecretBytes = bytesEncryptor.decrypt(cipherBytes);
                    return new WebhookTarget(config.getId(), config.getTargetUrl(),
                            new String(decryptedSecretBytes, StandardCharsets.UTF_8));
                })
                .toList();
    }

    @Override
    public List<UUID> listActiveMerchantIds() {
        return merchantRepository.findByStatus(MerchantStatus.ACTIVE)
                .stream().map(Merchant::getId).toList();
    }

    @Override
    public SettlementBankDetails getSettlementBankDetails(UUID merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", merchantId));

        return new SettlementBankDetails(
                merchant.getSettlementBankAccount(),
                merchant.getSettlementIfsc(),
                merchant.getSettlementBankAccountHolderName()
        );
    }
}
