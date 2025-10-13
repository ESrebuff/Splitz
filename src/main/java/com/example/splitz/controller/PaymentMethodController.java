package com.example.splitz.controller;

import com.example.splitz.dto.payment.PaymentMethodCreateDTO;
import com.example.splitz.dto.payment.PaymentMethodResponseDTO;
import com.example.splitz.mapper.PayementMethodMapper;
import com.example.splitz.model.PaymentMethod;
import com.example.splitz.service.PaymentMethodService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
public class PaymentMethodController {

    @Autowired
    private PaymentMethodService paymentMethodService;

    // Add a new payment method for the current user
    @PostMapping
    public ResponseEntity<PaymentMethodResponseDTO> addPaymentMethod(@RequestBody PaymentMethodCreateDTO dto) {
        String username = getAuthenticatedUsername();
        PaymentMethod created = paymentMethodService.addPaymentMethod(username, dto);
        return ResponseEntity.ok(PayementMethodMapper.toDTO(created));
    }

    // Get all payment methods of the authenticated user
    @GetMapping
    public ResponseEntity<List<PaymentMethodResponseDTO>> getMyPaymentMethods() {
        String username = getAuthenticatedUsername();
        List<PaymentMethodResponseDTO> methods = paymentMethodService.getUserPaymentMethods(username);
        return ResponseEntity.ok(methods);
    }

    // Get payment methods of another user (if authorized)
    @GetMapping("/user/{targetUserId}")
    public ResponseEntity<?> getUserPaymentMethods(
            @PathVariable Integer targetUserId) {

        String username = getAuthenticatedUsername();
        List<PaymentMethod> methods = paymentMethodService.getAuthorizedPaymentMethods(username, targetUserId);
        return ResponseEntity.ok(methods);
    }

    // Delete a payment method (if owned by current user)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable Integer id) {
        String username = getAuthenticatedUsername();
        paymentMethodService.deletePaymentMethod(username, id);
        return ResponseEntity.noContent().build();
    }

    private String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}

// TODO idées intéressantes : - partager des photos style rib -> pouvoir ajouter
// des dépences sans connexion internet