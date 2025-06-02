package com.example.splitz.dto.pot;

import com.example.splitz.model.PotType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PotCreateDTO {
    private String name;
    private Integer budget;
    private PotType type;
    private Integer fixedAmountPerUser;
    private Integer eventId;

}
