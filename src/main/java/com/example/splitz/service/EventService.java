package com.example.splitz.service;

import com.example.splitz.dto.EventCreateDTO;
import com.example.splitz.dto.EventGetDTO;
import com.example.splitz.model.Event;
import com.example.splitz.model.RoleEvent;
import com.example.splitz.model.User;
import com.example.splitz.model.UserEvent;
import com.example.splitz.repository.EventRepository;
import com.example.splitz.repository.UserEventRepository;
import com.example.splitz.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {

        @Autowired
        private EventRepository eventRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private UserEventRepository userEventRepository;

        @Autowired
        private UserService userService;

        public Event createEvent(EventCreateDTO eventCreateDTO, String username) {
                User user = userService.getUserByUsername(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Event event = new Event();
                event.setEventName(eventCreateDTO.getEventName());
                event.setEventDate(eventCreateDTO.getEventDate());
                event.setUser(user);
                event.setInviteCode(generateInviteCode());
                event.setInviteCodeExpiresAt(LocalDateTime.now().plusHours(1));

                Event createdEvent = eventRepository.save(event);

                associateUserToEvent(user, createdEvent, RoleEvent.ORGANIZER);

                return createdEvent;
        }

        public List<EventGetDTO> getAllEvents() {
                return eventRepository.findAll().stream()
                                .map(event -> EventGetDTO.builder()
                                                .id(event.getId())
                                                .eventName(event.getEventName())
                                                .eventDate(event.getEventDate())
                                                .inviteCode(event.getInviteCode())
                                                .inviteCodeExpiresAt(event.getInviteCodeExpiresAt())
                                                .createdAt(event.getCreatedAt())
                                                .updatedAt(event.getUpdatedAt())
                                                .organizerUsername(event.getUser().getUsername())
                                                .build())
                                .toList();
        }

        public EventGetDTO joinEventByInviteCode(String inviteCode, String username) {
                Event event = eventRepository.findByInviteCode(inviteCode)
                                .orElseThrow(() -> new EntityNotFoundException("Code d'invitation invalide"));

                if (event.getInviteCodeExpiresAt() != null &&
                                event.getInviteCodeExpiresAt().isBefore(LocalDateTime.now())) {
                        throw new IllegalStateException("Le code d'invitation a expiré");
                }

                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

                associateUserToEvent(user, event, RoleEvent.PARTICIPANT);

                return EventGetDTO.builder()
                                .id(event.getId())
                                .eventName(event.getEventName())
                                .eventDate(event.getEventDate())
                                .inviteCode(event.getInviteCode())
                                .inviteCodeExpiresAt(event.getInviteCodeExpiresAt())
                                .createdAt(event.getCreatedAt())
                                .updatedAt(event.getUpdatedAt())
                                .organizerUsername(event.getUser().getUsername())
                                .build();
        }

        @Transactional
        public void deleteEvent(Integer eventId, String username) {
                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new EntityNotFoundException("Événement non trouvé"));

                // Vérifier si l'utilisateur connecté est l'organisateur de l'événement
                if (!event.getUser().getUsername().equals(username)) {
                        throw new IllegalStateException(
                                        "L'utilisateur connecté n'est pas l'organisateur de cet événement");
                }

                eventRepository.delete(event);
        }

        @Transactional
        public void leaveEvent(Integer eventId, String username) {
                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new EntityNotFoundException("Événement non trouvé"));

                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

                // Ne pas permettre à l'organisateur de quitter l'événement
                if (event.getUser().getUsername().equals(username)) {
                        throw new IllegalStateException("L'organisateur ne peut pas quitter son propre événement");
                }

                UserEvent userEvent = userEventRepository.findByUserAndEvent(user, event)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "L'utilisateur n'est pas associé à cet événement"));

                userEventRepository.delete(userEvent);
        }

        @Transactional
        public Event updateEvent(Integer eventId, EventCreateDTO updatedEventDTO, String username) {
                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new EntityNotFoundException("Événement non trouvé"));

                if (!event.getUser().getUsername().equals(username)) {
                        throw new IllegalStateException("Seul l'organisateur peut modifier cet événement");
                }

                event.setEventName(updatedEventDTO.getEventName());
                event.setEventDate(updatedEventDTO.getEventDate());
                event.setUpdatedAt(LocalDateTime.now());

                return eventRepository.save(event);
        }

        public List<EventGetDTO> getUserEvents(String username) {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

                List<UserEvent> userEvents = userEventRepository.findAll()
                                .stream()
                                .filter(ue -> ue.getUser().getId().equals(user.getId()))
                                .toList();

                return userEvents.stream()
                                .map(ue -> {
                                        Event event = ue.getEvent();
                                        return EventGetDTO.builder()
                                                        .id(event.getId())
                                                        .eventName(event.getEventName())
                                                        .eventDate(event.getEventDate())
                                                        .inviteCode(event.getInviteCode())
                                                        .inviteCodeExpiresAt(event.getInviteCodeExpiresAt())
                                                        .createdAt(event.getCreatedAt())
                                                        .updatedAt(event.getUpdatedAt())
                                                        .organizerUsername(event.getUser().getUsername())
                                                        .build();
                                }).toList();
        }

        public List<String> getUsersByEventId(Integer eventId) {
                eventRepository.findById(eventId)
                                .orElseThrow(() -> new EntityNotFoundException("Événement non trouvé"));

                return userEventRepository.findAll()
                                .stream()
                                .filter(ue -> ue.getEvent().getId().equals(eventId))
                                .map(ue -> ue.getUser().getUsername())
                                .toList();
        }

        public void removeUserFromEvent(Integer eventId, String usernameToRemove, String username) {
                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new EntityNotFoundException("Événement non trouvé"));

                // Vérifier si l'utilisateur connecté est l'organisateur
                if (!event.getUser().getUsername().equals(username)) {
                        throw new IllegalStateException(
                                        "L'utilisateur connecté n'est pas l'organisateur de cet événement");
                }

                User userToRemove = userRepository.findByUsername(usernameToRemove)
                                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

                // Vérifier si l'utilisateur est bien associé à cet événement
                UserEvent userEvent = userEventRepository.findByUserAndEvent(userToRemove, event)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "L'utilisateur n'est pas associé à cet événement"));

                userEventRepository.delete(userEvent);
        }

        private void associateUserToEvent(User user, Event event, RoleEvent role) {
                // Vérifier si l'utilisateur est déjà associé à l'événement
                boolean alreadyAssociated = userEventRepository.existsByUserAndEvent(user, event);
                if (alreadyAssociated) {
                        throw new IllegalStateException("L'utilisateur est déjà associé à cet événement");
                }

                // Créer une nouvelle association
                UserEvent userEvent = new UserEvent();
                userEvent.setUser(user);
                userEvent.setEvent(event);
                userEvent.setRole(role);

                userEventRepository.save(userEvent);
        }

        private String generateInviteCode() {
                return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

}

// TODO : récupérer tous les pot d'un evenement
// TODO : quitter un événement

// TODO : à revoir pour ajouter un service