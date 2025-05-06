package com.example.splitz.repository;

import com.example.splitz.model.PayementMethod;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PayementMethodRepository extends JpaRepository<PayementMethod, Integer> {
    List<PayementMethod> findByUser_Username(String username);

    List<PayementMethod> findByUser_Id(Integer userId);
}
