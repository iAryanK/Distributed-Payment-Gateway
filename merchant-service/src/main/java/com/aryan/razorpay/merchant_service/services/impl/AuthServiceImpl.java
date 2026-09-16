package com.aryan.razorpay.merchant_service.services.impl;

import com.aryan.razorpay.common_lib.enums.UserRole;
import com.aryan.razorpay.common_lib.exceptions.BusinessRuleViolationException;
import com.aryan.razorpay.common_lib.exceptions.DuplicateResourceException;
import com.aryan.razorpay.common_lib.exceptions.ResourceNotFoundException;
import com.aryan.razorpay.merchant_service.dto.request.LoginRequest;
import com.aryan.razorpay.merchant_service.dto.request.MerchantSignupRequest;
import com.aryan.razorpay.merchant_service.dto.response.LoginResponse;
import com.aryan.razorpay.merchant_service.dto.response.MerchantResponse;
import com.aryan.razorpay.merchant_service.entities.AppUser;
import com.aryan.razorpay.merchant_service.entities.Merchant;
import com.aryan.razorpay.merchant_service.mapper.MerchantMapper;
import com.aryan.razorpay.merchant_service.repositories.AppUserRepository;
import com.aryan.razorpay.merchant_service.repositories.MerchantRepository;
import com.aryan.razorpay.merchant_service.security.JwtUtil;
import com.aryan.razorpay.merchant_service.services.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MerchantRepository merchantRepository;
    private final AppUserRepository appUserRepository;
    private final MerchantMapper merchantMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public MerchantResponse signup(MerchantSignupRequest request) {
        if (merchantRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Merchant with email "+request.email()+" already exists",
                                                "DUPLICATE_MERCHANT_EMAIL");
        }

        Merchant merchant = merchantMapper.toEntityFromSignUpRequest(request);
        merchant = merchantRepository.save(merchant);

        AppUser appUser = AppUser.builder()
                .email(request.email())
                .merchant(merchant)
                .password(passwordEncoder.encode(request.password()))
                .role(UserRole.OWNER)
                .build();
        appUserRepository.save(appUser);

        return merchantMapper.toResponse(merchant);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        AppUser appUser = appUserRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.email()));

        if (!passwordEncoder.matches(request.password(), appUser.getPassword())) {
            throw new BusinessRuleViolationException("INVALID_CREDENTIALS", "Invalid email or password");
        }

        String token = jwtUtil.generateAccessToken(request.email(), appUser.getMerchant().getId(), appUser.getRole().toString());

        return new LoginResponse(token);
    }
}
