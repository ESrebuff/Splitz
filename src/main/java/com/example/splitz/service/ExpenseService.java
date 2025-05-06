package com.example.splitz.service;

import com.example.splitz.model.Expense;
import com.example.splitz.model.Event;
import com.example.splitz.model.User;
import com.example.splitz.repository.ExpenseRepository;
import com.example.splitz.repository.EventRepository;
import com.example.splitz.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

        @Autowired
        private ExpenseRepository expenseRepository;

        @Autowired
        private EventRepository eventRepository;

        @Autowired
        private UserRepository userRepository;

        public Expense addExpense(Integer eventId, Integer amount, String description, Integer potId, String username) {
                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

                Expense expense = Expense.builder()
                                .event(event)
                                .paidByUserid(user.getId())
                                .amount(amount)
                                .description(description)
                                .potId(potId)
                                .build();

                return expenseRepository.save(expense);
        }

        public List<Expense> getExpensesByEvent(Integer eventId) {
                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

                return expenseRepository.findByEvent(event);
        }
}

// TODO : dire qui est concerné par la dépense