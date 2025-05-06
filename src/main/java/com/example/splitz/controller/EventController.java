package com.example.splitz.controller;

import com.example.splitz.dto.EventCreateDTO;
import com.example.splitz.dto.EventGetDTO;
import com.example.splitz.dto.UserEventDTO;
import com.example.splitz.model.Event;
import com.example.splitz.service.EventManagementService;
import com.example.splitz.service.EventParticipationService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventManagementService eventManagementService;

    @Autowired
    private EventParticipationService eventParticipationService;

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody @Valid EventCreateDTO dto) {
        String username = getAuthenticatedUsername();
        Event event = eventManagementService.createEvent(dto, username);
        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<Event> updateEvent(@PathVariable Integer eventId,
            @RequestBody @Valid EventCreateDTO dto) {
        String username = getAuthenticatedUsername();
        Event updated = eventManagementService.updateEvent(eventId, dto, username);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer eventId) {
        String username = getAuthenticatedUsername();
        eventManagementService.deleteEvent(eventId, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<EventGetDTO>> getAllEvents() {
        List<EventGetDTO> events = eventManagementService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @PostMapping("/join")
    public ResponseEntity<EventGetDTO> joinEvent(@RequestParam String inviteCode) {
        String username = getAuthenticatedUsername();
        EventGetDTO dto = eventParticipationService.joinEventByInviteCode(inviteCode, username);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/leave/{eventId}")
    public ResponseEntity<Void> leaveEvent(@PathVariable Integer eventId) {
        String username = getAuthenticatedUsername();
        eventParticipationService.leaveEvent(eventId, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventGetDTO>> getMyEvents() {
        String username = getAuthenticatedUsername();
        List<EventGetDTO> events = eventParticipationService.getUserEvents(username);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{eventId}/users")
    public ResponseEntity<List<UserEventDTO>> getUsersByEvent(@PathVariable Integer eventId) {
        List<UserEventDTO> userEventDTOs = eventParticipationService.getUsersByEventId(eventId);
        return ResponseEntity.ok(userEventDTOs);
    }

    @DeleteMapping("/{eventId}/user")
    public ResponseEntity<Void> removeUserFromEvent(@PathVariable Integer eventId,
            @RequestParam String usernameToRemove) {
        String username = getAuthenticatedUsername();
        eventParticipationService.removeUserFromEvent(eventId, usernameToRemove, username);
        return ResponseEntity.noContent().build();
    }

    private String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
