package com.example.splitz.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.splitz.dto.pot.PotCreateDTO;
import com.example.splitz.dto.pot.PotUpdateDTO;
import com.example.splitz.model.Event;
import com.example.splitz.model.Expense;
import com.example.splitz.model.Pot;
import com.example.splitz.model.PotType;
import com.example.splitz.model.UserPot;
import com.example.splitz.repository.EventRepository;
import com.example.splitz.repository.ExpenseRepository;
import com.example.splitz.repository.PotRepository;
import com.example.splitz.repository.UserPotRepository;

@Service
public class PotManagementService {

    @Autowired
    private PotRepository potRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserPotRepository userPotRepository;

    public Pot createPot(PotCreateDTO dto) {
        Pot pot = new Pot();
        pot.setName(dto.getName());
        pot.setType(dto.getType());
        pot.setBudget(dto.getType() == PotType.TARGET ? dto.getBudget() : null);
        pot.setFixedAmountPerUser(dto.getType() == PotType.FIXED ? dto.getFixedAmountPerUser() : null);
        pot.setEvent(getEventById(dto.getEventId()));
        return potRepository.save(pot);
    }

    public List<Pot> getPotsByEventId(Integer eventId) {
        return potRepository.findByEventId(eventId);
    }

    public Pot updatePot(Integer potId, PotUpdateDTO potUpdateDTO, String currentUsername) {
        Pot pot = potRepository.findById(potId)
                .orElseThrow(() -> new IllegalStateException("Pot introuvable"));

        if (!pot.getEvent().getUser().getUsername().equals(currentUsername)) {
            throw new IllegalStateException("Vous n'êtes pas l'organisateur de ce pot");
        }

        if (potUpdateDTO.getBudget() != null) {
            pot.setBudget(potUpdateDTO.getBudget());
        }
        if (potUpdateDTO.getName() != null) {
            pot.setName(potUpdateDTO.getName());
        }

        return potRepository.save(pot);
    }

    public void deletePot(Integer potId, String currentUsername) {
        Pot pot = potRepository.findById(potId)
                .orElseThrow(() -> new IllegalStateException("Pot introuvable"));

        if (!pot.getEvent().getUser().getUsername().equals(currentUsername)) {
            throw new IllegalStateException("Vous n'êtes pas l'organisateur de ce pot");
        }

        potRepository.delete(pot);
    }

    private Event getEventById(Integer eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
    }

    // Combien d'argent il reste dans un pot (budget - dépenses)
    public int getRemainingAmountInPot(Integer potId) {
        Pot pot = potRepository.findById(potId).orElseThrow(() -> new RuntimeException("Pot not found"));
        List<Expense> expenses = expenseRepository.findByPot_Id(potId);
        int totalExpenses = expenses.stream().mapToInt(Expense::getAmount).sum();
        return pot.getBudget() - totalExpenses;
    }

    // Combien d'argent il y a dans un pot (somme payée par tous les utilisateurs)
    public int getAmountCollectedInPot(Integer potId) {
        List<UserPot> userPots = userPotRepository.findByPot_Id(potId);
        return userPots.stream().mapToInt(UserPot::getAmountPaid).sum();
    }

}
