package com.example.splitz.controller;

import com.example.splitz.dto.pot.PotCreateDTO;
import com.example.splitz.dto.pot.PotUpdateDTO;
import com.example.splitz.model.Pot;
import com.example.splitz.model.User;
import com.example.splitz.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    // Create a new pot (e.g. shared fund for event)
    @PostMapping
    public ResponseEntity<Pot> createPot(@RequestBody @Valid PotCreateDTO potCreateDTO) {
        Pot createdPot = potService.createPot(potCreateDTO);
        return new ResponseEntity<>(createdPot, HttpStatus.CREATED);
    }

    // Organizer adds a user to a pot manually
    @PostMapping("/{potId}/add-user")
    public ResponseEntity<Void> addUserToPotAsOrganizer(
            @PathVariable Integer potId,
            @RequestParam String usernameToAdd) {

        String currentUsername = getAuthenticatedUsername();
        potParticipationService.addUserToPotAsOrganizer(potId, usernameToAdd, currentUsername);
        return ResponseEntity.ok().build();
    }

    // Authenticated user joins a pot
    @PostMapping("/{potId}/join")
    public ResponseEntity<Void> joinPot(@PathVariable Integer potId) {
        String username = getAuthenticatedUsername();
        potParticipationService.joinPot(potId, username);
        return ResponseEntity.ok().build();
    }

    // Contribute an amount to a pot (current user)
    @PostMapping("/{potId}/contribute")
    public ResponseEntity<Void> contribute(
            @PathVariable Integer potId,
            @RequestParam Integer amount) {

        String username = getAuthenticatedUsername();
        potParticipationService.contributeToPot(potId, username, amount);
        return ResponseEntity.ok().build();
    }

    // Get all pots associated with an event
    @GetMapping("/by-event/{eventId}")
    public ResponseEntity<List<Pot>> getPotsByEvent(@PathVariable Integer eventId) {
        List<Pot> pots = potService.getPotsByEventId(eventId);
        return ResponseEntity.ok(pots);
    }

    // Get users participating in a specific pot
    @GetMapping("/{potId}/users")
    public ResponseEntity<List<User>> getUsersByPot(@PathVariable Integer potId) {
        List<User> users = potParticipationService.getUsersByPot(potId);
        return ResponseEntity.ok(users);
    }

    // Update pot details (by the organizer)
    @PutMapping("/{potId}")
    public ResponseEntity<Pot> updatePot(@PathVariable Integer potId,
            @RequestBody PotUpdateDTO potUpdateDTO) {
        String currentUsername = getAuthenticatedUsername();
        Pot updatedPot = potService.updatePot(potId, potUpdateDTO, currentUsername);
        return new ResponseEntity<>(updatedPot, HttpStatus.OK);
    }

    // Leave a pot (authenticated user)
    @DeleteMapping("/{potId}/leave")
    public ResponseEntity<Void> leavePot(@PathVariable Integer potId) {
        String currentUsername = getAuthenticatedUsername();
        potParticipationService.leavePot(potId, currentUsername);
        return ResponseEntity.noContent().build();
    }

    // Delete a pot (by the organizer)
    @DeleteMapping("/{potId}")
    public ResponseEntity<Void> deletePot(@PathVariable Integer potId) {
        String currentUsername = getAuthenticatedUsername();
        potService.deletePot(potId, currentUsername);
        return ResponseEntity.noContent().build();
    }

    // Get all pots of the current user
    @GetMapping("/my")
    public ResponseEntity<List<Pot>> getMyPots() {
        String username = getAuthenticatedUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        List<Pot> pots = potParticipationService.getPotsByUserId(user.getId());
        return ResponseEntity.ok(pots);
    }

    // Get pot analytics - how much a user has paid in a pot
    @GetMapping("/{potId}/user/{userId}/amount-paid")
    public ResponseEntity<Integer> getAmountPaidByUser(@PathVariable Integer potId, @PathVariable Integer userId) {
        int amount = potParticipationService.getAmountPaidByUserInPot(userId, potId);
        return ResponseEntity.ok(amount);
    }

    // Get pot analytics - how much a user owes in a pot
    @GetMapping("/{potId}/user/{userId}/amount-owed")
    public ResponseEntity<Integer> getAmountOwedByUser(@PathVariable Integer potId, @PathVariable Integer userId) {
        int amount = potParticipationService.getAmountOwedByUserInPot(userId, potId);
        return ResponseEntity.ok(amount);
    }

    // Get pot analytics - total amount in pot
    @GetMapping("/{potId}/total-amount")
    public ResponseEntity<Integer> getTotalAmountInPot(@PathVariable Integer potId) {
        int amount = potParticipationService.getTotalAmountInPot(potId);
        return ResponseEntity.ok(amount);
    }

    // Get pot analytics - remaining amount in pot
    @GetMapping("/{potId}/remaining-amount")
    public ResponseEntity<Integer> getRemainingAmountInPot(@PathVariable Integer potId) {
        int amount = potParticipationService.getRemainingAmountInPot(potId);
        return ResponseEntity.ok(amount);
    }

    private String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
