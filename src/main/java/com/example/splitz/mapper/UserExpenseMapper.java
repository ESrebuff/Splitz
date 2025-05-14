package com.example.splitz.mapper;

import com.example.splitz.dto.UserExpenseDTO;
import com.example.splitz.model.Expense;
import com.example.splitz.model.User;
import com.example.splitz.model.UserExpense;

public class UserExpenseMapper {

    public static UserExpense toEntity(UserExpenseDTO dto, Expense expense, User user) {
        return UserExpense.builder()
                .expense(expense)
                .user(user)
                .amountOwed(dto.getAmountOwed())
                .build();
    }
}
