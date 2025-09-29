package com.example.splitz.repository;

import com.example.splitz.model.UserExpense;
import com.example.splitz.model.Expense;
import com.example.splitz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserExpenseRepository extends JpaRepository<UserExpense, Integer> {
    List<UserExpense> findByExpense(Expense expense);

    List<UserExpense> findByUser(User user);

    List<UserExpense> findByUser_Id(Integer userId);
}