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

        public Expense getExpenseById(Integer expenseId) {
                return expenseRepository.findById(expenseId)
                                .orElseThrow(() -> new RuntimeException("Dépense non trouvée"));
        }

        public List<Expense> getExpensesByUser(String username) {
                User user = getUserByUsername(username);
                List<UserExpense> userExpenses = userExpenseRepository.findByUser(user);
                return userExpenses.stream()
                                .map(UserExpense::getExpense)
                                .distinct()
                                .toList();
        }

        public Expense updateExpense(Integer expenseId, ExpenseCreateDTO dto, String username) {
                Expense existingExpense = getExpenseById(expenseId);
                User user = getUserByUsername(username);

                // Vérifier que l'utilisateur peut modifier cette dépense
                if (!existingExpense.getPaidByUserid().equals(user.getId())) {
                        throw new IllegalStateException("Vous ne pouvez modifier que vos propres dépenses");
                }

                // Valider les montants
                if (dto.getUserSplits() != null && !dto.getUserSplits().isEmpty()) {
                        int totalSplitAmount = dto.getUserSplits().stream()
                                        .mapToInt(UserExpenseDTO::getAmountOwed)
                                        .sum();

                        if (totalSplitAmount != dto.getAmount()) {
                                throw new IllegalArgumentException(
                                                "La somme des montants dus ne correspond pas au montant total.");
                        }
                }

                // Mettre à jour les champs
                existingExpense.setAmount(dto.getAmount());
                existingExpense.setDescription(dto.getDescription());
                existingExpense.setPotId(dto.getPotId());

                Expense savedExpense = expenseRepository.save(existingExpense);

                // Mettre à jour les UserExpense si fournis
                if (dto.getUserSplits() != null && !dto.getUserSplits().isEmpty()) {
                        // Supprimer les anciens UserExpense
                        List<UserExpense> existingUserExpenses = userExpenseRepository.findByExpense(existingExpense);
                        userExpenseRepository.deleteAll(existingUserExpenses);

                        // Créer les nouveaux UserExpense
                        for (UserExpenseDTO userSplit : dto.getUserSplits()) {
                                User splitUser = userRepository.findById(userSplit.getUserId())
                                                .orElseThrow(() -> new RuntimeException(
                                                                "Utilisateur non trouvé (id: " + userSplit.getUserId()
                                                                                + ")"));

                                UserExpense userExpense = UserExpenseMapper.toEntity(userSplit, savedExpense,
                                                splitUser);
                                userExpenseRepository.save(userExpense);
                        }
                }

                return savedExpense;
        }

        public void deleteExpense(Integer expenseId, String username) {
                Expense expense = getExpenseById(expenseId);
                User user = getUserByUsername(username);

                // Vérifier que l'utilisateur peut supprimer cette dépense
                if (!expense.getPaidByUserid().equals(user.getId())) {
                        throw new IllegalStateException("Vous ne pouvez supprimer que vos propres dépenses");
                }

                // Supprimer les UserExpense associés
                List<UserExpense> userExpenses = userExpenseRepository.findByExpense(expense);
                userExpenseRepository.deleteAll(userExpenses);

                // Supprimer la dépense
                expenseRepository.delete(expense);
        }

        private User getUserByUsername(String username) {
                return userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        }

}
