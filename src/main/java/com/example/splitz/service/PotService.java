package com.example.splitz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.splitz.dto.PotCreateDTO;
import com.example.splitz.model.Pot;
import com.example.splitz.repository.PotRepository;

@Service
public class PotService {

    @Autowired
    private PotRepository potRepository;

    public Pot createPot(PotCreateDTO potCreateDTO) {
        Pot pot = new Pot();
        pot.setName(potCreateDTO.getName());
        pot.setBudget(potCreateDTO.getBudget());
        return potRepository.save(pot);
    }

}

// TODO : Ajouter un user à un pot en tant qu'organisateur
// TODO : modifier un pot en tant qu'organisateur
// TODO : rejoindre un pot
// TODO : partir d'un pot
// TODO : connaitre combien un utilisateur a payé dans un pot
// TODO : connaitre combien un utilisateur doit payer dans un pot
// TODO : connaitre combien d'argent il reste dans un pot
// TODO : connaitre combien d'argent il y a dans un pot
// TODO : récupérer tous les pots d'un utilisateur

// TODO : à revoir pour ajouter un service