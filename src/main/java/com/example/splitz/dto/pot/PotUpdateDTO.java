package com.example.splitz.dto.pot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PotUpdateDTO {
    private String name;
    private Integer budget;
}
