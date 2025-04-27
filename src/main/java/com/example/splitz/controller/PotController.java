package com.example.splitz.controller;

import com.example.splitz.dto.PotCreateDTO;
import com.example.splitz.model.Pot;
import com.example.splitz.service.PotService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pots")
public class PotController {

    @Autowired
    private PotService potService;

    @PostMapping
    public ResponseEntity<Pot> createPot(@RequestBody @Valid PotCreateDTO potCreateDTO) {
        Pot createdPot = potService.createPot(potCreateDTO);
        return new ResponseEntity<>(createdPot, HttpStatus.CREATED);
    }
}