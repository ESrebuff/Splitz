package com.example.splitz.controller;

import com.example.splitz.dto.EventCreateDTO;
import com.example.splitz.dto.EventGetDTO;
import com.example.splitz.model.Event;
import com.example.splitz.service.EventService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody @Valid EventCreateDTO eventCreateDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Event createdEvent = eventService.createEvent(eventCreateDTO, username);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @PostMapping("/join")
    public ResponseEntity<EventGetDTO> joinEvent(@RequestParam String inviteCode) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        EventGetDTO eventGetDTO = eventService.joinEventByInviteCode(inviteCode, username);
        return ResponseEntity.ok(eventGetDTO);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteEvent(@RequestParam Integer eventId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        eventService.deleteEvent(eventId, username);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user")
    public ResponseEntity<Void> removeUserFromEvent(@RequestParam Integer eventId,
            @RequestParam String usernameToRemove) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        eventService.removeUserFromEvent(eventId, usernameToRemove, username);
        return ResponseEntity.noContent().build();
    }
}
