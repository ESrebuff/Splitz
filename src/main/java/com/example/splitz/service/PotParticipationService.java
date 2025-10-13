package com.example.splitz.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.splitz.dto.pot.PotResponseDTO;
import com.example.splitz.mapper.PotMapper;
import com.example.splitz.model.Event;
import com.example.splitz.model.Expense;
import com.example.splitz.model.Pot;
import com.example.splitz.model.PotType;
import com.example.splitz.model.User;
import com.example.splitz.model.UserExpense;
import com.example.splitz.model.UserPot;
import com.example.splitz.repository.ExpenseRepository;
import com.example.splitz.repository.PotRepository;
import com.example.splitz.repository.UserEventRepository;
import com.example.splitz.repository.UserExpenseRepository;
import com.example.splitz.repository.UserPotRepository;
import com.example.splitz.repository.UserRepository;

@Service
public class PotParticipationService {

    @Autowired
    private PotRepository potRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserEventRepository userEventRepository;

    @Autowired
    private UserPotRepository userPotRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserExpenseRepository userExpenseRepository;

    public void addUserToPotAsOrganizer(Integer potId, String usernameToAdd, String currentUsername) {
        Pot pot = getPotOrThrow(potId);
        Event event = getEventOrThrow(pot);

        if (!event.getUser().getUsername().equals(currentUsername)) {
            throw new IllegalStateException("Vous n'êtes pas l'organisateur de l'événement lié à ce pot");
        }

        User userToAdd = getUserOrThrow(usernameToAdd);
        checkUserInEventOrThrow(userToAdd, event);
        checkUserNotInPotOrThrow(userToAdd, pot);

        saveUserPot(userToAdd, pot);
    }

    public void joinPot(Integer potId, String currentUsername) {
        Pot pot = getPotOrThrow(potId);
        Event event = getEventOrThrow(pot);
        User user = getUserOrThrow(currentUsername);

        checkUserInEventOrThrow(user, event);
        checkUserNotInPotOrThrow(user, pot);

        UserPot userPot = new UserPot();
        userPot.setUser(user);
        userPot.setPot(pot);

        if (pot.getType() == PotType.FIXED) {
            if (pot.getFixedAmountPerUser() == null) {
                throw new IllegalStateException("Montant fixe non défini pour ce pot");
            }
            userPot.setAmountPaid(pot.getFixedAmountPerUser());
        } else {
            userPot.setAmountPaid(0);
        }

        userPotRepository.save(userPot);
    }

    public void leavePot(Integer potId, String currentUsername) {
        Pot pot = potRepository.findById(potId)
                .orElseThrow(() -> new NoSuchElementException("Pot introuvable"));

        User user = getUserOrThrow(currentUsername);

        UserPot userPot = getUserPotOrThrow(user, pot);

        userPotRepository.delete(userPot);
    }

    public List<User> getUsersByPot(Integer potId) {
        Pot pot = getPotOrThrow(potId);

        return userPotRepository.findAllByPot(pot)
                .stream()
                .map(UserPot::getUser)
                .collect(Collectors.toList());
    }

    public void contributeToPot(Integer potId, String username, Integer amount) {
        Pot pot = getPotOrThrow(potId);
        if (pot.getType() != PotType.TARGET) {
            throw new IllegalStateException("Ce pot n'accepte pas de contributions variables.");
        }

        User user = getUserOrThrow(username);
        UserPot userPot = getUserPotOrThrow(user, pot);

        userPot.setAmountPaid(userPot.getAmountPaid() + amount);
        userPotRepository.save(userPot);
    }

    // Combien un utilisateur a payé dans un pot
    public int getAmountPaidByUserInPot(Integer userId, Integer potId) {
        List<UserPot> userPots = userPotRepository.findByUser_IdAndPot_Id(userId, potId);
        return userPots.stream().mapToInt(UserPot::getAmountPaid).sum();
    }

    // Combien un utilisateur doit payer dans un pot
    public int getAmountOwedByUserInPot(Integer userId, Integer potId) {
        List<UserExpense> userExpenses = userExpenseRepository.findByUser_Id(userId);
        return userExpenses.stream()
                .filter(ue -> ue.getExpense().getPotId() != null && ue.getExpense().getPotId().equals(potId))
                .mapToInt(UserExpense::getAmountOwed)
                .sum();
    }

    // Récupérer tous les pots d'un utilisateur
    public List<PotResponseDTO> getPotsByUserId(Integer userId) {
        List<UserPot> userPots = userPotRepository.findByUser_Id(userId);

        return userPots.stream()
                .map(UserPot::getPot) // récupère le Pot
                .map(PotMapper::toDTO) // transforme en DTO
                .collect(Collectors.toList());
    }

    // Combien d'argent il y a dans un pot (total des contributions)
    public int getTotalAmountInPot(Integer potId) {
        List<UserPot> userPots = userPotRepository.findByPotId(potId);
        return userPots.stream().mapToInt(UserPot::getAmountPaid).sum();
    }

    // Combien d'argent il reste dans un pot (total - dépenses)
    public int getRemainingAmountInPot(Integer potId) {
        getPotOrThrow(potId); // Vérifier que le pot existe
        int totalContributions = getTotalAmountInPot(potId);
        int totalExpenses = getTotalExpensesFromPot(potId);
        return totalContributions - totalExpenses;
    }

    // Calculer le total des dépenses payées avec ce pot
    private int getTotalExpensesFromPot(Integer potId) {
        List<Expense> expenses = expenseRepository.findByPotId(potId);
        return expenses.stream().mapToInt(Expense::getAmount).sum();
    }

    private Pot getPotOrThrow(Integer potId) {
        return potRepository.findById(potId)
                .orElseThrow(() -> new NoSuchElementException("Pot introuvable"));
    }

    private UserPot getUserPotOrThrow(User user, Pot pot) {
        return userPotRepository.findByUserAndPot(user, pot)
                .orElseThrow(() -> new IllegalStateException("Vous n'êtes pas membre de ce pot"));
    }

    private Event getEventOrThrow(Pot pot) {
        Event event = pot.getEvent();
        if (event == null) {
            throw new IllegalStateException("Ce pot n'est lié à aucun événement");
        }
        return event;
    }

    private User getUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Utilisateur non trouvé"));
    }

    private void checkUserInEventOrThrow(User user, Event event) {
        if (!userEventRepository.existsByUserAndEvent(user, event)) {
            throw new IllegalStateException("L'utilisateur n'est pas associé à cet événement");
        }
    }

    private void checkUserNotInPotOrThrow(User user, Pot pot) {
        if (userPotRepository.existsByUserAndPot(user, pot)) {
            throw new IllegalStateException("L'utilisateur est déjà dans ce pot");
        }
    }

    private void saveUserPot(User user, Pot pot) {
        UserPot userPot = new UserPot();
        userPot.setUser(user);
        userPot.setPot(pot);
        userPot.setAmountPaid(0);
        userPotRepository.save(userPot);
    }

}