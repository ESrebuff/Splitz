package com.example.splitz.mapper;

import com.example.splitz.dto.payment.PaymentMethodResponseDTO;
import com.example.splitz.model.PaymentMethod;

public class PayementMethodMapper {
    public static PaymentMethodResponseDTO toDTO(PaymentMethod paymentMethod) {
        return PaymentMethodResponseDTO.builder()
                .id(paymentMethod.getId())
                .method(paymentMethod.getMethod())
                .accountInfo(paymentMethod.getAccountInfo())
                .build();
    }
}
