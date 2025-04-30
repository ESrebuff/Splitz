package com.example.splitz.controller;

import com.example.splitz.dto.PotCreateDTO;
import com.example.splitz.dto.PotUpdateDTO;
import com.example.splitz.model.Pot;
import com.example.splitz.service.PotParticipationService;
import com.example.splitz.service.PotManagementService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pots")
public class PotController {

    @Autowired
    private PotManagementService potService;

    @Autowired
    private PotParticipationService potParticipationService;

    @PostMapping
    public ResponseEntity<Pot> createPot(@RequestBody @Valid PotCreateDTO potCreateDTO) {
        Pot createdPot = potService.createPot(potCreateDTO);
        return new ResponseEntity<>(createdPot, HttpStatus.CREATED);
    }

    @PostMapping("/{potId}/add-user")
    public ResponseEntity<Void> addUserToPotAsOrganizer(
            @PathVariable Integer potId,
            @RequestParam String usernameToAdd) {

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        potParticipationService.addUserToPotAsOrganizer(potId, usernameToAdd, currentUsername);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{potId}/join")
    public ResponseEntity<Void> joinPot(@PathVariable Integer potId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        potParticipationService.joinPot(potId, username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/by-event/{eventId}")
    public ResponseEntity<List<Pot>> getPotsByEvent(@PathVariable Integer eventId) {
        List<Pot> pots = potService.getPotsByEventId(eventId);
        return ResponseEntity.ok(pots);
    }

    @PutMapping("/{potId}")
    public ResponseEntity<Pot> updatePot(@PathVariable Integer potId,
            @RequestBody PotUpdateDTO potUpdateDTO) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Pot updatedPot = potService.updatePot(potId, potUpdateDTO, currentUsername);
        return new ResponseEntity<>(updatedPot, HttpStatus.OK);
    }

    @DeleteMapping("/{potId}/leave")
    public ResponseEntity<Void> leavePot(@PathVariable Integer potId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        potParticipationService.leavePot(potId, currentUsername);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{potId}")
    public ResponseEntity<Void> deletePot(@PathVariable Integer potId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        potService.deletePot(potId, currentUsername);
        return ResponseEntity.noContent().build();
    }
}