package com.example.splitz.mapper;

import com.example.splitz.dto.pot.PotCreateDTO;
import com.example.splitz.dto.pot.PotResponseDTO;
import com.example.splitz.model.Pot;

public class PotMapper {

    public static PotResponseDTO toDTO(Pot pot) {
        return PotResponseDTO.builder()

                .id(pot.getId())
                .name(pot.getName())
                .budget(pot.getBudget())
                .build();
    }

    public static Pot fromCreateDTO(PotCreateDTO dto) {
        Pot pot = new Pot();
        pot.setName(dto.getName());
        pot.setBudget(null);
        return pot;
    }
}
