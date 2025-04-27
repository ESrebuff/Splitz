package com.example.splitz.repository;

import com.example.splitz.model.Event;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> {
    Optional<Event> findByInviteCode(String inviteCode);
}
