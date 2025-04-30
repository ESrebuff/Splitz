package com.example.splitz.repository;

import com.example.splitz.model.Pot;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PotRepository extends JpaRepository<Pot, Integer> {
    List<Pot> findByEventId(Integer eventId);
}
