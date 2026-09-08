package com.aryan.razorpay.merchant_service.controllers;

import com.aryan.razorpay.merchant_service.dto.request.LoginRequest;
import com.aryan.razorpay.merchant_service.dto.request.MerchantSignupRequest;
import com.aryan.razorpay.merchant_service.dto.response.LoginResponse;
import com.aryan.razorpay.merchant_service.dto.response.MerchantResponse;
import com.aryan.razorpay.merchant_service.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MerchantResponse> signup(@RequestBody @Valid MerchantSignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                authService.signup(request)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(
                authService.login(request)
        );
    }

}
