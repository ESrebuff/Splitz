package com.example.splitz.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.splitz.dto.PotCreateDTO;
import com.example.splitz.dto.PotUpdateDTO;
import com.example.splitz.model.Pot;
import com.example.splitz.repository.PotRepository;

@Service
public class PotManagementService {

    @Autowired
    private PotRepository potRepository;

    public Pot createPot(PotCreateDTO potCreateDTO) {
        Pot pot = new Pot();
        pot.setName(potCreateDTO.getName());
        pot.setBudget(potCreateDTO.getBudget());
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

}

// TODO : connaitre combien un utilisateur a payé dans un pot
// TODO : connaitre combien un utilisateur doit payer dans un pot
// TODO : connaitre combien d'argent il reste dans un pot
// TODO : connaitre combien d'argent il y a dans un pot
// TODO : récupérer tous les pots d'un utilisateur
