package com.aryan.razorpay.merchant_service.services;

import com.aryan.razorpay.merchant_service.dto.request.LoginRequest;
import com.aryan.razorpay.merchant_service.dto.request.MerchantSignupRequest;
import com.aryan.razorpay.merchant_service.dto.response.LoginResponse;
import com.aryan.razorpay.merchant_service.dto.response.MerchantResponse;
import jakarta.validation.Valid;

public interface AuthService {
    MerchantResponse signup(MerchantSignupRequest request);

    LoginResponse login(@Valid LoginRequest request);
}
