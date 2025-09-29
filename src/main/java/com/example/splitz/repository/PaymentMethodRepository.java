package com.example.splitz.repository;

import com.example.splitz.model.PaymentMethod;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {
    List<PaymentMethod> findByUser_Username(String username);

    List<PaymentMethod> findByUser_Id(Integer userId);
}
