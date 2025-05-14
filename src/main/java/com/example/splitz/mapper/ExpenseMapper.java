package com.example.splitz.mapper;

import com.example.splitz.dto.ExpenseResponseDTO;
import com.example.splitz.model.Expense;
import com.example.splitz.model.Event;
import com.example.splitz.model.User;

public class ExpenseMapper {
    public static Expense toEntity(Event event, User payer, int amount, String description, Integer potId) {
        return Expense.builder()
                .event(event)
                .paidByUserid(payer.getId())
                .amount(amount)
                .description(description)
                .potId(potId)
                .build();
    }

    public static ExpenseResponseDTO toDto(Expense expense) {
        ExpenseResponseDTO dto = new ExpenseResponseDTO();
        dto.setId(expense.getId());
        dto.setAmount(expense.getAmount());
        dto.setDescription(expense.getDescription());
        dto.setPaidByUserid(expense.getPaidByUserid());
        dto.setEventId(expense.getEvent().getId());
        dto.setPotId(expense.getPotId());
        return dto;
    }
}
