package com.example.splitz.dto.expense;

import java.util.List;

import com.example.splitz.dto.UserExpenseDTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseCreateDTO {

    @NotNull(message = "L'ID de l'événement est obligatoire")
    @Positive(message = "L'ID de l'événement doit être positif")
    private Integer eventId;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    @Max(value = 999999, message = "Le montant ne peut pas dépasser 999,999€")
    private Integer amount;

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 255, message = "La description ne peut pas dépasser 255 caractères")
    private String description;

    @Positive(message = "L'ID du pot doit être positif")
    private Integer potId;

    @NotEmpty(message = "Au moins un participant doit être spécifié")
    @Size(max = 50, message = "Maximum 50 participants par dépense")
    private List<UserExpenseDTO> userSplits;
}
