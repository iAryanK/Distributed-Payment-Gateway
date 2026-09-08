package com.aryan.razorpay.merchant_service.services.impl;

import com.aryan.razorpay.common_lib.exceptions.ResourceNotFoundException;
import com.aryan.razorpay.common_lib.util.RandomizedUtil;
import com.aryan.razorpay.merchant_service.dto.request.UpdateWebhookConfigRequest;
import com.aryan.razorpay.merchant_service.dto.response.WebhookConfigResponse;
import com.aryan.razorpay.merchant_service.entities.Merchant;
import com.aryan.razorpay.merchant_service.entities.MerchantWebhookConfig;
import com.aryan.razorpay.merchant_service.mapper.WebhookConfigMapper;
import com.aryan.razorpay.merchant_service.repositories.MerchantRepository;
import com.aryan.razorpay.merchant_service.repositories.WebhookConfigRepository;
import com.aryan.razorpay.merchant_service.services.WebhookConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookConfigServiceImpl implements WebhookConfigService {

    private final MerchantRepository merchantRepository;
    private final WebhookConfigRepository webhookConfigRepository;
    private final BytesEncryptor bytesEncryptor;
    private final WebhookConfigMapper webhookConfigMapper;

    @Override
    public WebhookConfigResponse create(UUID merchantId, UpdateWebhookConfigRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", merchantId));

        String rawSecret = RandomizedUtil.randomBase64(32);
        byte[] rawSecretBytes = rawSecret.getBytes(StandardCharsets.UTF_8);
        String encryptedSecret = Base64.getEncoder().encodeToString(bytesEncryptor.encrypt(rawSecretBytes));

        MerchantWebhookConfig config = MerchantWebhookConfig.builder()
                .merchant(merchant)
                .targetUrl(request.targetUrl())
                .enabled(true)
                .eventTypes(request.eventTypes())
                .webhookSecret(encryptedSecret)
                .build();
        config  = webhookConfigRepository.save(config);

        return webhookConfigMapper.toResponse(config, rawSecret);
    }

    @Override
    public List<WebhookConfigResponse> list(UUID merchantId) {
        return webhookConfigRepository.findByMerchant_Id(merchantId).stream()
                .map(config -> webhookConfigMapper.toResponse(config, null))
                .toList();
    }

    @Override
    public WebhookConfigResponse getById(UUID merchantId, UUID configId) {
        MerchantWebhookConfig config = requireOwnedConfig(merchantId, configId);
        return webhookConfigMapper.toResponse(config, null);
    }

    @Override
    @Transactional
    public WebhookConfigResponse update(UUID merchantId, UUID configId, UpdateWebhookConfigRequest request) {
        MerchantWebhookConfig config = requireOwnedConfig(merchantId, configId);
        config.setTargetUrl(request.targetUrl());
        config.setEventTypes(request.eventTypes());
        return webhookConfigMapper.toResponse(config, null);
    }

    @Override
    @Transactional
    public void delete(UUID merchantId, UUID configId) {
        MerchantWebhookConfig config = requireOwnedConfig(merchantId, configId);
        webhookConfigRepository.delete(config);
    }

    private MerchantWebhookConfig requireOwnedConfig(UUID merchantId, UUID configId) {
        return webhookConfigRepository.findByIdAndMerchant_Id(configId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("MerchantWebhookConfig", configId));
    }
}
