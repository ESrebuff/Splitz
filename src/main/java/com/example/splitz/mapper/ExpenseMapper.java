package com.example.splitz.mapper;

import com.example.splitz.model.Expense;
import com.example.splitz.model.User;
import com.example.splitz.dto.expense.ExpenseCreateDTO;
import com.example.splitz.dto.expense.ExpenseResponseDTO;
import com.example.splitz.model.Event;

public class ExpenseMapper {

    public static ExpenseResponseDTO toDto(Expense expense) {
        return ExpenseResponseDTO.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .paidByUserid(expense.getPaidByUserid())
                .eventId(expense.getEvent().getId())
                .potId(expense.getPotId())
                .build();

    }

    public static Expense toEntity(Event event, User payer, ExpenseCreateDTO dto) {
        Expense expense = new Expense();
        expense.setEvent(event);
        expense.setPaidByUserid(payer.getId());
        expense.setAmount(dto.getAmount());
        expense.setDescription(dto.getDescription());
        expense.setPotId(dto.getPotId());
        return expense;
    }
}
