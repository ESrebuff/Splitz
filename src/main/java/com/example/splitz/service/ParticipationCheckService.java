package com.example.splitz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.splitz.repository.UserEventRepository;
import com.example.splitz.repository.UserPotRepository;

@Service
public class ParticipationCheckService {

    @Autowired
    private UserEventRepository userEventRepository;

    @Autowired
    private UserPotRepository userPotRepository;

    public boolean shareEventOrPot(Integer userId1, Integer userId2) {
        boolean sameEvent = userEventRepository.findByUser_Id(userId1).stream()
                .anyMatch(ue1 -> userEventRepository.existsByEventAndUser_Id(ue1.getEvent(), userId2));

        if (sameEvent)
            return true;

        boolean samePot = userPotRepository.findByUser_Id(userId1).stream()
                .anyMatch(up1 -> userPotRepository.existsByPotAndUser_Id(up1.getPot(), userId2));

        return samePot;
    }
}