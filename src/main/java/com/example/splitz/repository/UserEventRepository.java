package com.example.splitz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.splitz.model.Event;
import com.example.splitz.model.User;
import com.example.splitz.model.UserEvent;

public interface UserEventRepository extends JpaRepository<UserEvent, Integer> {
    boolean existsByUserAndEvent(User user, Event event);

    Optional<UserEvent> findByUserAndEvent(User user, Event event);
}
