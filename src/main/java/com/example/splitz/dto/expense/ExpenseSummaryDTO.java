package com.example.splitz.dto.expense;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseSummaryDTO {
    private Integer id;
    private Integer amount;
    private String description;
}
