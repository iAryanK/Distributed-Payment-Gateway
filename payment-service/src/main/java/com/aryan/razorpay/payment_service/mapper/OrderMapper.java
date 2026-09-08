package com.aryan.razorpay.payment_service.mapper;

import com.aryan.razorpay.payment_service.dto.response.OrderResponse;
import com.aryan.razorpay.payment_service.entities.OrderRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    OrderResponse toResponse(OrderRecord order);
}
