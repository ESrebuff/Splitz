package com.example.splitz.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserExpenseDTO {

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    @Positive(message = "L'ID de l'utilisateur doit être positif")
    private Integer userId;

    @NotNull(message = "Le montant dû est obligatoire")
    @PositiveOrZero(message = "Le montant dû doit être positif ou zéro")
    @Max(value = 999999, message = "Le montant dû ne peut pas dépasser 999,999€")
    private Integer amountOwed;
}