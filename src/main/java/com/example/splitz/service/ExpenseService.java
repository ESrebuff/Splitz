package com.example.splitz.service;

import com.example.splitz.model.Expense;
import com.example.splitz.model.Pot;
import com.example.splitz.dto.UserExpenseDTO;
import com.example.splitz.dto.expense.ExpenseCreateDTO;
import com.example.splitz.mapper.ExpenseMapper;
import com.example.splitz.mapper.UserExpenseMapper;
import com.example.splitz.model.Event;
import com.example.splitz.model.User;
import com.example.splitz.model.UserExpense;
import com.example.splitz.repository.ExpenseRepository;
import com.example.splitz.repository.PotRepository;
import com.example.splitz.repository.UserExpenseRepository;
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
        private PotRepository potRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private UserExpenseRepository userExpenseRepository;

        public Expense addExpense(ExpenseCreateDTO dto, String username) {
                if (dto.getUserSplits().isEmpty()) {
                        throw new IllegalArgumentException(
                                        "La liste des utilisateurs concernés ne peut pas être vide.");
                }

                Event event = getEventById(dto.getEventId());
                User payer = getUserByUsername(username);

                int totalSplitAmount = dto.getUserSplits().stream()
                                .mapToInt(UserExpenseDTO::getAmountOwed)
                                .sum();

                if (totalSplitAmount != dto.getAmount()) {
                        throw new IllegalArgumentException(
                                        "La somme des montants dus ne correspond pas au montant total.");
                }

                Expense expense = ExpenseMapper.toEntity(event, payer, dto);
                Expense savedExpense = expenseRepository.save(expense);

                for (UserExpenseDTO userSplit : dto.getUserSplits()) {
                        User user = userRepository.findById(userSplit.getUserId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Utilisateur non trouvé (id: " + userSplit.getUserId() + ")"));

                        UserExpense userExpense = UserExpenseMapper.toEntity(userSplit, savedExpense, user);
                        userExpenseRepository.save(userExpense);
                }

                return savedExpense;
        }

        public Expense usePotForExpense(ExpenseCreateDTO dto, String username) {
                Event event = getEventById(dto.getEventId());
                User payer = getUserByUsername(username);

                Pot pot = potRepository.findById(dto.getPotId())
                                .orElseThrow(() -> new RuntimeException("Cagnotte non trouvée"));

                if (!pot.getEvent().getId().equals(dto.getEventId())) {
                        throw new IllegalArgumentException("La cagnotte ne correspond pas à l'événement spécifié.");
                }

                Expense expense = ExpenseMapper.toEntity(event, payer, dto);
                return expenseRepository.save(expense);
        }

        public List<Expense> getExpensesByEvent(Integer eventId) {
                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
                return expenseRepository.findByEvent(event);
        }

        private Event getEventById(Integer eventId) {
                return eventRepository.findById(eventId)
                                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        }

        private User getUserByUsername(String username) {
                return userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        }

}
