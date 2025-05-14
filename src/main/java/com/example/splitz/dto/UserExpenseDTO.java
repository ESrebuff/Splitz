package com.example.splitz.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class UserExpenseDTO {

    @NotNull
    private Integer userId;

    @NotNull
    @Min(0)
    private Integer amountOwed;
}