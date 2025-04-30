package com.example.splitz.service;

import com.example.splitz.dto.EventGetDTO;
import com.example.splitz.helper.EventAssociationHelper;
import com.example.splitz.mapper.EventMapper;
import com.example.splitz.model.Event;
import com.example.splitz.model.RoleEvent;
import com.example.splitz.model.User;
import com.example.splitz.model.UserEvent;
import com.example.splitz.repository.EventRepository;
import com.example.splitz.repository.UserEventRepository;
import com.example.splitz.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventParticipationService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserEventRepository userEventRepository;

    @Autowired
    private EventAssociationHelper eventAssociationHelper;

    public EventGetDTO joinEventByInviteCode(String inviteCode, String username) {
        Event event = eventRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new EntityNotFoundException("Code invalide"));

        if (event.getInviteCodeExpiresAt() != null && event.getInviteCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Code expiré");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        eventAssociationHelper.associateUserToEvent(user, event, RoleEvent.PARTICIPANT);
        return EventMapper.toDTO(event);
    }

    public void leaveEvent(Integer eventId, String username) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Événement non trouvé"));

        if (event.getUser().getUsername().equals(username)) {
            throw new IllegalStateException("L'organisateur ne peut pas quitter son événement");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        UserEvent ue = userEventRepository.findByUserAndEvent(user, event)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non associé à l'événement"));

        userEventRepository.delete(ue);
    }

    public void removeUserFromEvent(Integer eventId, String targetUsername, String requesterUsername) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Événement non trouvé"));

        if (!event.getUser().getUsername().equals(requesterUsername)) {
            throw new IllegalStateException("Non autorisé");
        }

        User user = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur à supprimer introuvable"));

        UserEvent ue = userEventRepository.findByUserAndEvent(user, event)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non dans l'événement"));

        userEventRepository.delete(ue);
    }

    public List<EventGetDTO> getUserEvents(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        return userEventRepository.findAll().stream()
                .filter(ue -> ue.getUser().getId().equals(user.getId()))
                .map(ue -> EventMapper.toDTO(ue.getEvent()))
                .toList();
    }

    public List<String> getUsersByEventId(Integer eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Événement non trouvé"));

        return userEventRepository.findAll().stream()
                .filter(ue -> ue.getEvent().getId().equals(eventId))
                .map(ue -> ue.getUser().getUsername())
                .toList();
    }
}
