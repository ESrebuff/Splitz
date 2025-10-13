package com.example.splitz.repository;

import com.example.splitz.model.Expense;
import com.example.splitz.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    List<Expense> findByEvent(Event event);

    List<Expense> findByPotId(Integer potId);
}
