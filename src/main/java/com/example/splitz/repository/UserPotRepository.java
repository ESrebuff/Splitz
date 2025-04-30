package com.example.splitz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.splitz.model.Pot;
import com.example.splitz.model.User;
import com.example.splitz.model.UserPot;

public interface UserPotRepository extends JpaRepository<UserPot, Integer> {
    boolean existsByUserAndPot(User user, Pot pot);

    Optional<UserPot> findByUserAndPot(User user, Pot pot);

}
