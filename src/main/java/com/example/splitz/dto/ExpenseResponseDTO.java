package com.example.splitz.dto;

import lombok.Data;

@Data
public class ExpenseResponseDTO {
    private Integer id;
    private Integer amount;
    private String description;
    private Integer paidByUserid;
    private Integer eventId;
    private Integer potId;
}
