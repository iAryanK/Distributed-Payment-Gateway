package com.aryan.razorpay.merchant_service.mapper;

import com.aryan.razorpay.merchant_service.dto.request.MerchantSignupRequest;
import com.aryan.razorpay.merchant_service.dto.response.MerchantResponse;
import com.aryan.razorpay.merchant_service.entities.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MerchantMapper {

    Merchant toEntityFromSignUpRequest(MerchantSignupRequest merchantSignupRequest);

    MerchantResponse toResponse(Merchant merchant);

}

