package com.example.splitz.dto.expense;

import java.util.List;

import com.example.splitz.dto.UserExpenseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseCreateDTO {
    private Integer eventId;
    private Integer amount;
    private String description;
    private Integer potId;
    private List<UserExpenseDTO> userSplits;
}
