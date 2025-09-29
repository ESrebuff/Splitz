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

    // Add an expense with manual participant shares
    @PostMapping("/manual")
    public ResponseEntity<ExpenseResponseDTO> addCustomSplitExpense(@RequestBody @Valid ExpenseCreateDTO dto) {
        String username = getAuthenticatedUsername();
        Expense expense = expenseService.addExpense(dto, username);
        ExpenseResponseDTO responseDto = ExpenseMapper.toDto(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // Add an expense using a shared pot
    @PostMapping("/with-pot")
    public ResponseEntity<ExpenseResponseDTO> createExpenseWithPot(@RequestBody @Valid ExpenseCreateDTO dto) {
        String username = getAuthenticatedUsername();
        Expense expense = expenseService.usePotForExpense(dto, username);
        ExpenseResponseDTO responseDto = ExpenseMapper.toDto(expense);
        return ResponseEntity.ok(responseDto);
    }

    // Get all expenses related to a given event
    @GetMapping("/by-event/{eventId}")
    public ResponseEntity<List<ExpenseResponseDTO>> getExpensesByEvent(@PathVariable Integer eventId) {
        List<Expense> expenses = expenseService.getExpensesByEvent(eventId);
        List<ExpenseResponseDTO> response = expenses.stream()
                .map(ExpenseMapper::toDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    // Get a specific expense by ID
    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponseDTO> getExpenseById(@PathVariable Integer expenseId) {
        Expense expense = expenseService.getExpenseById(expenseId);
        ExpenseResponseDTO responseDto = ExpenseMapper.toDto(expense);
        return ResponseEntity.ok(responseDto);
    }

    // Get all expenses of the current user
    @GetMapping("/my")
    public ResponseEntity<List<ExpenseResponseDTO>> getMyExpenses() {
        String username = getAuthenticatedUsername();
        List<Expense> expenses = expenseService.getExpensesByUser(username);
        List<ExpenseResponseDTO> response = expenses.stream()
                .map(ExpenseMapper::toDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    // Update an existing expense
    @PutMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponseDTO> updateExpense(
            @PathVariable Integer expenseId,
            @RequestBody @Valid ExpenseCreateDTO dto) {
        String username = getAuthenticatedUsername();
        Expense updatedExpense = expenseService.updateExpense(expenseId, dto, username);
        ExpenseResponseDTO responseDto = ExpenseMapper.toDto(updatedExpense);
        return ResponseEntity.ok(responseDto);
    }

    // Delete an expense
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Integer expenseId) {
        String username = getAuthenticatedUsername();
        expenseService.deleteExpense(expenseId, username);
        return ResponseEntity.noContent().build();
    }

    private String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
