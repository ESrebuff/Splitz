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
