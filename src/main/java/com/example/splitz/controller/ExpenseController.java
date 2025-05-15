package com.example.splitz.controller;

import com.example.splitz.dto.expense.ExpenseResponseDTO;
import com.example.splitz.dto.expense.ExpenseCreateDTO;
import com.example.splitz.mapper.ExpenseMapper;
import com.example.splitz.model.Expense;
import com.example.splitz.service.ExpenseService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/manual")
    public ResponseEntity<ExpenseResponseDTO> addCustomSplitExpense(@RequestBody @Valid ExpenseCreateDTO dto) {
        String username = getAuthenticatedUsername();
        Expense expense = expenseService.addExpense(dto, username);
        ExpenseResponseDTO responseDto = ExpenseMapper.toDto(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/with-pot")
    public ResponseEntity<ExpenseResponseDTO> createExpenseWithPot(@RequestBody @Valid ExpenseCreateDTO dto) {
        String username = getAuthenticatedUsername();
        Expense expense = expenseService.usePotForExpense(dto, username);
        ExpenseResponseDTO responseDto = ExpenseMapper.toDto(expense);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/by-event/{eventId}")
    public ResponseEntity<List<ExpenseResponseDTO>> getExpensesByEvent(@PathVariable Integer eventId) {
        List<Expense> expenses = expenseService.getExpensesByEvent(eventId);
        List<ExpenseResponseDTO> response = expenses.stream()
                .map(ExpenseMapper::toDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    private String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
