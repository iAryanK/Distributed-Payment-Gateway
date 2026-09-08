package com.aryan.razorpay.merchant_service.controllers;

import com.aryan.razorpay.common_lib.context.MerchantContext;
import com.aryan.razorpay.merchant_service.dto.request.CreateApiKeyRequest;
import com.aryan.razorpay.merchant_service.dto.response.ApiKeyCreateResponse;
import com.aryan.razorpay.merchant_service.dto.response.ApiKeyResponse;
import com.aryan.razorpay.merchant_service.services.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/merchants/api-keys")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<ApiKeyCreateResponse> create(@Valid @RequestBody CreateApiKeyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiKeyService.create(merchantContext.getMerchantId(), request));
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> listByMerchant() {
        return ResponseEntity.ok(apiKeyService.listByMerchant(merchantContext.getMerchantId()));
    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity<Void> revoke(@PathVariable UUID keyId) {
        apiKeyService.revoke(merchantContext.getMerchantId(), keyId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<ApiKeyCreateResponse> rotateKey(@PathVariable UUID keyId) {
        return ResponseEntity.ok(apiKeyService.rotate(merchantContext.getMerchantId(), keyId));
    }
}
