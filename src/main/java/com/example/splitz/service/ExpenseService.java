package com.example.splitz.service;

import com.example.splitz.model.Expense;
import com.example.splitz.model.Pot;
import com.example.splitz.dto.UserExpenseDTO;
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

        public Expense addExpenseWith(Integer eventId, Integer amount, String description,
                        String username, List<UserExpenseDTO> splits) {

                if (splits.isEmpty()) {
                        throw new IllegalArgumentException(
                                        "La liste des utilisateurs concernés ne peut pas être vide.");
                }

                Event event = getEventById(eventId);
                User payer = getUserByUsername(username);

                int totalSplitAmount = splits.stream()
                                .mapToInt(UserExpenseDTO::getAmountOwed)
                                .sum();

                if (totalSplitAmount != amount) {
                        throw new IllegalArgumentException(
                                        "La somme des montants dus ne correspond pas au montant total.");
                }

                Expense expense = ExpenseMapper.toEntity(event, payer, amount, description, null);

                Expense savedExpense = expenseRepository.save(expense);

                for (UserExpenseDTO dto : splits) {
                        User user = userRepository.findById(dto.getUserId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Utilisateur non trouvé (id: " + dto.getUserId() + ")"));

                        UserExpense userExpense = UserExpenseMapper.toEntity(dto, savedExpense, user);
                        userExpenseRepository.save(userExpense);
                }

                return savedExpense;
        }

        public Expense usePotForExpense(Integer eventId, Integer amount, String description,
                        Integer potId, String username) {

                Event event = getEventById(eventId);
                User payer = getUserByUsername(username);

                Pot pot = potRepository.findById(potId)
                                .orElseThrow(() -> new RuntimeException("Cagnotte non trouvé"));

                if (!pot.getEvent().getId().equals(eventId)) {
                        throw new IllegalArgumentException("La cagnotte ne correspond pas à l'événement spécifié.");
                }

                Expense expense = ExpenseMapper.toEntity(event, payer, amount, description, potId);

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
