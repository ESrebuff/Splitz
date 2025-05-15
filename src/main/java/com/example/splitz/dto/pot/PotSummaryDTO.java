package com.example.splitz.dto.pot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PotSummaryDTO {
    private Integer id;
    private String name;
    private Integer totalAmount;
}
