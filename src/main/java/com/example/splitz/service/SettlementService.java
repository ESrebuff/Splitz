package com.example.splitz.service;

import com.example.splitz.model.Event;
import com.example.splitz.model.Expense;
import com.example.splitz.model.UserExpense;
import com.example.splitz.repository.EventRepository;
import com.example.splitz.repository.ExpenseRepository;
import com.example.splitz.repository.UserExpenseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SettlementService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserExpenseRepository userExpenseRepository;

    /**
     * Calcule les remboursements optimaux pour un événement
     * Utilise l'algorithme de minimisation des transactions
     */
    public List<SettlementTransaction> calculateOptimalSettlement(Integer eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        // Récupérer toutes les dépenses de l'événement
        List<Expense> expenses = expenseRepository.findByEvent(event);

        // Calculer le solde net de chaque utilisateur
        Map<Integer, Integer> userBalances = calculateUserBalances(expenses);

        // Séparer les créanciers (solde positif) et les débiteurs (solde négatif)
        List<UserBalance> creditors = new ArrayList<>();
        List<UserBalance> debtors = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : userBalances.entrySet()) {
            Integer userId = entry.getKey();
            Integer balance = entry.getValue();

            if (balance > 0) {
                creditors.add(new UserBalance(userId, balance));
            } else if (balance < 0) {
                debtors.add(new UserBalance(userId, -balance)); // Convertir en positif
            }
        }

        // Trier par montant décroissant pour optimiser
        creditors.sort((a, b) -> Integer.compare(b.amount, a.amount));
        debtors.sort((a, b) -> Integer.compare(b.amount, a.amount));

        // Calculer les transactions optimales
        return calculateOptimalTransactions(creditors, debtors);
    }

    /**
     * Calcule le solde net de chaque utilisateur dans l'événement
     */
    private Map<Integer, Integer> calculateUserBalances(List<Expense> expenses) {
        Map<Integer, Integer> balances = new HashMap<>();

        for (Expense expense : expenses) {
            // Ajouter ce que l'utilisateur a payé
            Integer payerId = expense.getPaidByUserid();
            balances.merge(payerId, expense.getAmount(), Integer::sum);

            // Soustraire ce que chaque utilisateur doit
            List<UserExpense> userExpenses = userExpenseRepository.findByExpense(expense);
            for (UserExpense userExpense : userExpenses) {
                Integer userId = userExpense.getUser().getId();
                Integer amountOwed = userExpense.getAmountOwed();
                balances.merge(userId, -amountOwed, Integer::sum);
            }
        }

        return balances;
    }

    /**
     * Calcule les transactions optimales entre créanciers et débiteurs
     */
    private List<SettlementTransaction> calculateOptimalTransactions(
            List<UserBalance> creditors, List<UserBalance> debtors) {

        List<SettlementTransaction> transactions = new ArrayList<>();
        int creditorIndex = 0;
        int debtorIndex = 0;

        while (creditorIndex < creditors.size() && debtorIndex < debtors.size()) {
            UserBalance creditor = creditors.get(creditorIndex);
            UserBalance debtor = debtors.get(debtorIndex);

            int transactionAmount = Math.min(creditor.amount, debtor.amount);

            if (transactionAmount > 0) {
                transactions.add(new SettlementTransaction(
                        debtor.userId, creditor.userId, transactionAmount));
            }

            // Mettre à jour les montants restants
            creditor.amount -= transactionAmount;
            debtor.amount -= transactionAmount;

            // Passer au suivant si le montant est épuisé
            if (creditor.amount == 0)
                creditorIndex++;
            if (debtor.amount == 0)
                debtorIndex++;
        }

        return transactions;
    }

    /**
     * Classe interne pour représenter le solde d'un utilisateur
     */
    private static class UserBalance {
        Integer userId;
        Integer amount;

        UserBalance(Integer userId, Integer amount) {
            this.userId = userId;
            this.amount = amount;
        }
    }

    /**
     * Classe pour représenter une transaction de remboursement
     */
    public static class SettlementTransaction {
        private Integer fromUserId;
        private Integer toUserId;
        private Integer amount;

        public SettlementTransaction(Integer fromUserId, Integer toUserId, Integer amount) {
            this.fromUserId = fromUserId;
            this.toUserId = toUserId;
            this.amount = amount;
        }

        // Getters
        public Integer getFromUserId() {
            return fromUserId;
        }

        public Integer getToUserId() {
            return toUserId;
        }

        public Integer getAmount() {
            return amount;
        }

        @Override
        public String toString() {
            return String.format("User %d doit %d€ à User %d", fromUserId, amount, toUserId);
        }
    }
}
