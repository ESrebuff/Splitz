package com.example.splitz.controller;

import jakarta.validation.Valid;

import com.example.splitz.dto.LoginRequestDTO;
import com.example.splitz.dto.UpdatePasswordDTO;
import com.example.splitz.dto.UpdateUserInfoDTO;
import com.example.splitz.dto.UserCreateDTO;
import com.example.splitz.model.User;
import com.example.splitz.security.JwtUtil;
import com.example.splitz.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody @Valid UserCreateDTO userCreateDTO) {
        User createdUser = userService.createUser(userCreateDTO);
        String token = jwtUtil.generateToken(createdUser.getUsername());
        return new ResponseEntity<>(token, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginDTO) {
        return userService.authenticate(loginDTO.getUsername(), loginDTO.getPassword())
                .map(user -> ResponseEntity.ok(jwtUtil.generateToken(user.getUsername())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials"));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> requestAccountDeletion() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.markAccountForDeletion(username);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateUserInfo(@RequestBody UpdateUserInfoDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.updateUserInfo(username, dto);
        return ResponseEntity.ok("User info updated");
    }

    @PatchMapping("/me/password")
    public ResponseEntity<?> updatePassword(@RequestBody @Valid UpdatePasswordDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean updated = userService.updatePassword(username, dto);
        if (!updated) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect current password");
        }
        return ResponseEntity.ok("Password updated");
    }

}