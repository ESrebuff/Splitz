package com.example.splitz.dto.payment;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodResponseDTO {
    private Integer id;
    private String method;
    private JsonNode accountInfo;
}