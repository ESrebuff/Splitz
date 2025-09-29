package com.example.splitz.controller;

import com.example.splitz.dto.event.EventCreateDTO;
import com.example.splitz.dto.event.EventResponseDTO;
import com.example.splitz.dto.event.UserEventResponseDTO;
import com.example.splitz.dto.expense.ExpenseResponseDTO;
import com.example.splitz.mapper.EventMapper;
import com.example.splitz.mapper.ExpenseMapper;
import com.example.splitz.model.Event;
import com.example.splitz.model.Expense;
import com.example.splitz.model.Pot;
import com.example.splitz.service.EventManagementService;
import com.example.splitz.service.EventParticipationService;
import com.example.splitz.service.SettlementService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SettlementService settlementService;

    // Create a new event (only for the authenticated user)
    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(@RequestBody @Valid EventCreateDTO dto) {
        String username = getAuthenticatedUsername();
        Event event = eventManagementService.createEvent(dto, username);
        return ResponseEntity.ok(EventMapper.toDTO(event));
    }

    // Update an existing event (only if user is the creator)
    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponseDTO> updateEvent(@PathVariable Integer eventId,
            @RequestBody @Valid EventCreateDTO dto) {
        String username = getAuthenticatedUsername();
        Event updated = eventManagementService.updateEvent(eventId, dto, username);
        return ResponseEntity.ok(EventMapper.toDTO(updated));
    }

    // Delete an event (only if user is the creator)
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer eventId) {
        String username = getAuthenticatedUsername();
        eventManagementService.deleteEvent(eventId, username);
        return ResponseEntity.noContent().build();
    }

    // Get all events
    @GetMapping
    public ResponseEntity<List<EventResponseDTO>> getAllEvents() {
        List<EventResponseDTO> events = eventManagementService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    // Join an event using an invite code (authenticated user)
    @PostMapping("/join")
    public ResponseEntity<EventResponseDTO> joinEvent(@RequestParam String inviteCode) {
        String username = getAuthenticatedUsername();
        EventResponseDTO dto = eventParticipationService.joinEventByInviteCode(inviteCode, username);
        return ResponseEntity.ok(dto);
    }

    // Leave an event (authenticated user leaves participation)
    @DeleteMapping("/leave/{eventId}")
    public ResponseEntity<Void> leaveEvent(@PathVariable Integer eventId) {
        String username = getAuthenticatedUsername();
        eventParticipationService.leaveEvent(eventId, username);
        return ResponseEntity.noContent().build();
    }

    // Get all events the current user is participating in
    @GetMapping("/my")
    public ResponseEntity<List<EventResponseDTO>> getMyEvents() {
        String username = getAuthenticatedUsername();
        List<EventResponseDTO> events = eventParticipationService.getUserEvents(username);
        return ResponseEntity.ok(events);
    }

    // Get list of users participating in a specific event
    @GetMapping("/{eventId}/users")
    public ResponseEntity<List<UserEventResponseDTO>> getUsersByEvent(@PathVariable Integer eventId) {
        List<UserEventResponseDTO> userEventDTOs = eventParticipationService.getUsersByEventId(eventId);
        return ResponseEntity.ok(userEventDTOs);
    }

    // Remove a user from an event (only by the organizer)
    @DeleteMapping("/{eventId}/user")
    public ResponseEntity<Void> removeUserFromEvent(@PathVariable Integer eventId,
            @RequestParam String usernameToRemove) {
        String username = getAuthenticatedUsername();
        eventParticipationService.removeUserFromEvent(eventId, usernameToRemove, username);
        return ResponseEntity.noContent().build();
    }

    // Get a specific event by ID
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Integer eventId) {
        Event event = eventManagementService.getEventById(eventId);
        return ResponseEntity.ok(EventMapper.toDTO(event));
    }

    // Get all expenses of an event
    @GetMapping("/{eventId}/expenses")
    public ResponseEntity<List<ExpenseResponseDTO>> getEventExpenses(@PathVariable Integer eventId) {
        List<Expense> expenses = eventManagementService.getEventExpenses(eventId);
        List<ExpenseResponseDTO> response = expenses.stream()
                .map(ExpenseMapper::toDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    // Get all pots of an event
    @GetMapping("/{eventId}/pots")
    public ResponseEntity<List<Pot>> getEventPots(@PathVariable Integer eventId) {
        List<Pot> pots = eventManagementService.getEventPots(eventId);
        return ResponseEntity.ok(pots);
    }

    // Get optimal settlement for an event (who owes whom)
    @GetMapping("/{eventId}/settlement")
    public ResponseEntity<List<SettlementService.SettlementTransaction>> getOptimalSettlement(
            @PathVariable Integer eventId) {
        List<SettlementService.SettlementTransaction> settlement = settlementService
                .calculateOptimalSettlement(eventId);
        return ResponseEntity.ok(settlement);
    }

    private String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
