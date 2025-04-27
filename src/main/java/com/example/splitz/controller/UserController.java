package com.example.splitz.controller;

import jakarta.validation.Valid;

import com.example.splitz.dto.UserCreateDTO;
import com.example.splitz.model.User;
import com.example.splitz.security.JwtUtil;
import com.example.splitz.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

}