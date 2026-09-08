package com.aryan.razorpay.payment_service.controllers;

import com.aryan.razorpay.common_lib.context.MerchantContext;
import com.aryan.razorpay.payment_service.dto.request.CreateOrderRequest;
import com.aryan.razorpay.payment_service.dto.response.OrderResponse;
import com.aryan.razorpay.payment_service.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid CreateOrderRequest request) {
        OrderResponse orderResponse = orderService.create(merchantContext.getMerchantId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }
}
