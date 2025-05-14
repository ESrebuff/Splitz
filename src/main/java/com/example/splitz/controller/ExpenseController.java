package com.example.splitz.controller;

import com.example.splitz.dto.ExpenseManualDTO;
import com.example.splitz.dto.ExpenseResponseDTO;
import com.example.splitz.dto.ExpenseWithPotDTO;
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
    public ResponseEntity<ExpenseResponseDTO> addCustomSplitExpense(@RequestBody @Valid ExpenseManualDTO dto) {
        String username = getAuthenticatedUsername();
        Expense expense = expenseService.addExpenseWith(
                dto.getEventId(),
                dto.getAmount(),
                dto.getDescription(),
                username,
                dto.getUserSplits());

        ExpenseResponseDTO responseDto = ExpenseMapper.toDto(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/with-pot")
    public ResponseEntity<ExpenseResponseDTO> createExpenseWithPot(@RequestBody @Valid ExpenseWithPotDTO dto) {
        String username = getAuthenticatedUsername();
        Expense created = expenseService.usePotForExpense(
                dto.getEventId(),
                dto.getAmount(),
                dto.getDescription(),
                dto.getPotId(),
                username);

        ExpenseResponseDTO responseDto = ExpenseMapper.toDto(created);
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
