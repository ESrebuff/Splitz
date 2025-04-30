package com.example.splitz.service;

import com.example.splitz.dto.EventCreateDTO;
import com.example.splitz.dto.EventGetDTO;
import com.example.splitz.helper.EventAssociationHelper;
import com.example.splitz.mapper.EventMapper;
import com.example.splitz.model.Event;
import com.example.splitz.model.RoleEvent;
import com.example.splitz.model.User;
import com.example.splitz.repository.EventRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventManagementService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EventAssociationHelper eventAssociationHelper;

    public Event createEvent(EventCreateDTO dto, String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Event event = new Event();
        event.setEventName(dto.getEventName());
        event.setEventDate(dto.getEventDate());
        event.setUser(user);
        event.setInviteCode(generateInviteCode());
        event.setInviteCodeExpiresAt(LocalDateTime.now().plusHours(1));

        Event createdEvent = eventRepository.save(event);
        eventAssociationHelper.associateUserToEvent(user, createdEvent, RoleEvent.ORGANIZER);

        return createdEvent;
    }

    public List<EventGetDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(EventMapper::toDTO)
                .toList();
    }

    @Transactional
    public Event updateEvent(Integer eventId, EventCreateDTO dto, String username) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Événement non trouvé"));

        if (!event.getUser().getUsername().equals(username)) {
            throw new IllegalStateException("Seul l'organisateur peut modifier cet événement");
        }

        event.setEventName(dto.getEventName());
        event.setEventDate(dto.getEventDate());
        event.setUpdatedAt(LocalDateTime.now());

        return eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(Integer eventId, String username) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Événement non trouvé"));

        if (!event.getUser().getUsername().equals(username)) {
            throw new IllegalStateException("L'utilisateur connecté n'est pas l'organisateur");
        }

        eventRepository.delete(event);
    }

    private String generateInviteCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
