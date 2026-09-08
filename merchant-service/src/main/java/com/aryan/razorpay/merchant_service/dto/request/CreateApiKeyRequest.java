package com.aryan.razorpay.merchant_service.dto.request;

import com.aryan.razorpay.common_lib.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment
) {
}
