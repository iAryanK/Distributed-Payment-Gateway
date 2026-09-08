package com.aryan.razorpay.merchant_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateWebhookConfigRequest(

        @NotBlank(message = "webhook URL is required")
        @Size(max = 500)
        @Pattern(regexp = "^https?://.+", message = "webhook URL must be a valid http(s) URL")
        String targetUrl,

        // comma-separated fine-grained event type names (e.g. "PAYMENT_STATUS_CHANGED, REFUND_CREATED")
        // null/blank/"ALL" subscribes to every event type
        @Size(max = 1000)
        String eventTypes
) {
}
