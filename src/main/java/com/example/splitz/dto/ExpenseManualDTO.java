package com.example.splitz.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

import java.util.List;

@Data
public class ExpenseManualDTO {

    @NotNull
    private Integer eventId;

    @NotNull
    private Integer amount;

    @NotBlank
    private String description;

    @NotEmpty
    private List<UserExpenseDTO> userSplits;
}
