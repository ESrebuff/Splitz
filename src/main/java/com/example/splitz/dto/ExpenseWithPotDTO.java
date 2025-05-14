package com.example.splitz.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class ExpenseWithPotDTO {

    @NotNull
    private Integer eventId;

    @NotNull
    private Integer amount;

    @NotBlank
    private String description;

    @NotNull
    private Integer potId;
}
