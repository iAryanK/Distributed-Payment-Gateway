package com.aryan.razorpay.merchant_service.dto.response;

import com.aryan.razorpay.common_lib.enums.Environment;

import java.util.UUID;

public record ApiKeyCreateResponse(
        UUID id,
        String keyId,
        String keySecret,
        Environment environment
) {
}
