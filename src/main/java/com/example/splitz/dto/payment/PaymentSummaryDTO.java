package com.example.splitz.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSummaryDTO {
    private Integer id;
    private Integer amount;
    private String payerUsername;
    private String paymentDate;
}
