package com.example.splitz.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.splitz.model.Event;
import com.example.splitz.model.RoleEvent;
import com.example.splitz.model.User;
import com.example.splitz.model.UserEvent;
import com.example.splitz.repository.UserEventRepository;

@Component
public class EventAssociationHelper {

    @Autowired
    private UserEventRepository userEventRepository;

    public void associateUserToEvent(User user, Event event, RoleEvent role) {
        if (userEventRepository.existsByUserAndEvent(user, event)) {
            throw new IllegalStateException("Utilisateur déjà associé");
        }

        UserEvent userEvent = new UserEvent();
        userEvent.setUser(user);
        userEvent.setEvent(event);
        userEvent.setRole(role);

        userEventRepository.save(userEvent);
    }
}
