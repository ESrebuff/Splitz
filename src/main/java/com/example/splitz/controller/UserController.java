package com.example.splitz.controller;

import jakarta.validation.Valid;

import com.example.splitz.dto.user.LoginRequestDTO;
import com.example.splitz.dto.user.UserCreateDTO;
import com.example.splitz.dto.user.UserUpdateInfoDTO;
import com.example.splitz.dto.user.UserUpdatePasswordDTO;
import com.example.splitz.model.User;
import com.example.splitz.security.JwtUtil;
import com.example.splitz.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // Register a new user and return a JWT token
    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody @Valid UserCreateDTO userCreateDTO) {
        User createdUser = userService.createUser(userCreateDTO);
        String token = jwtUtil.generateToken(createdUser.getUsername());
        return new ResponseEntity<>(token, HttpStatus.CREATED);
    }

    // Log in with credentials and receive JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginDTO) {
        return userService.authenticate(loginDTO.getUsername(), loginDTO.getPassword())
                .map(user -> ResponseEntity.ok(jwtUtil.generateToken(user.getUsername())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials"));
    }

    // Get current user's info
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        String username = getAuthenticatedUsername();
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Update user's profile information
    @PatchMapping("/me")
    public ResponseEntity<?> updateUserInfo(@RequestBody UserUpdateInfoDTO dto) {
        String username = getAuthenticatedUsername();
        userService.updateUserInfo(username, dto);
        return ResponseEntity.ok("User info updated");
    }

    // Delete the current user account
    @DeleteMapping("/me")
    public ResponseEntity<Void> requestAccountDeletion() {
        String username = getAuthenticatedUsername();
        userService.markAccountForDeletion(username);
        return ResponseEntity.noContent().build();
    }

    // Update user's password
    @PatchMapping("/me/password")
    public ResponseEntity<?> updatePassword(@RequestBody @Valid UserUpdatePasswordDTO dto) {
        String username = getAuthenticatedUsername();
        boolean updated = userService.updatePassword(username, dto);
        if (!updated) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect current password");
        }
        return ResponseEntity.ok("Password updated");
    }

    private String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
