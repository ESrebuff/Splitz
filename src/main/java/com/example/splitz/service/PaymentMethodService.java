package com.example.splitz.service;

import com.example.splitz.dto.payment.PaymentMethodCreateDTO;
import com.example.splitz.dto.payment.PaymentMethodResponseDTO;
import com.example.splitz.mapper.PayementMethodMapper;
import com.example.splitz.model.PaymentMethod;
import com.example.splitz.model.User;
import com.example.splitz.repository.PaymentMethodRepository;
import com.example.splitz.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private ParticipationCheckService participationCheckService;

    public PaymentMethod addPaymentMethod(String username, PaymentMethodCreateDTO dto) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        PaymentMethod paymentMethod = PaymentMethod.builder()
                .user(user)
                .method(dto.getMethod())
                .accountInfo(dto.getAccountInfo())
                .build();

        return paymentMethodRepository.save(paymentMethod);
    }

    public List<PaymentMethodResponseDTO> getUserPaymentMethods(String username) {
        return paymentMethodRepository.findByUser_Username(username)
                .stream()
                .map(PayementMethodMapper::toDTO)
                .toList();

    }

    public void deletePaymentMethod(String username, Integer id) {
        PaymentMethod method = paymentMethodRepository.findById(id)
                .filter(pm -> pm.getUser().getUsername().equals(username))
                .orElseThrow(() -> new RuntimeException("Payment method not found or unauthorized"));
        paymentMethodRepository.delete(method);
    }

    public List<PaymentMethod> getAuthorizedPaymentMethods(String requesterUsername, Integer targetUserId) {
        User requester = userRepository.findByUsername(requesterUsername)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (requester.getId().equals(targetUserId)) {
            return paymentMethodRepository.findByUser_Id(targetUserId);
        }

        boolean canView = participationCheckService.shareEventOrPot(requester.getId(), targetUserId);
        if (!canView) {
            throw new SecurityException("Vous n'avez pas le droit de voir les moyens de paiement de cet utilisateur");
        }

        return paymentMethodRepository.findByUser_Id(targetUserId);
    }

}
