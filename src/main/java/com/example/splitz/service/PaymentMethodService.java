package com.example.splitz.service;

import com.example.splitz.dto.PaymentMethodCreateDTO;
import com.example.splitz.model.PayementMethod;
import com.example.splitz.model.User;
import com.example.splitz.repository.PayementMethodRepository;
import com.example.splitz.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PayementMethodRepository payementMethodRepository;

    @Autowired
    private ParticipationCheckService participationCheckService;

    public PayementMethod addPaymentMethod(String username, PaymentMethodCreateDTO dto) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        PayementMethod paymentMethod = PayementMethod.builder()
                .user(user)
                .method(dto.getMethod())
                .accountInfo(dto.getAccountInfo())
                .build();

        return payementMethodRepository.save(paymentMethod);
    }

    public List<PayementMethod> getUserPaymentMethods(String username) {
        return payementMethodRepository.findByUser_Username(username);
    }

    public void deletePaymentMethod(String username, Integer id) {
        PayementMethod method = payementMethodRepository.findById(id)
                .filter(pm -> pm.getUser().getUsername().equals(username))
                .orElseThrow(() -> new RuntimeException("Payment method not found or unauthorized"));
        payementMethodRepository.delete(method);
    }

    public List<PayementMethod> getAuthorizedPaymentMethods(String requesterUsername, Integer targetUserId) {
        User requester = userRepository.findByUsername(requesterUsername)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (requester.getId().equals(targetUserId)) {
            return payementMethodRepository.findByUser_Id(targetUserId);
        }

        boolean canView = participationCheckService.shareEventOrPot(requester.getId(), targetUserId);
        if (!canView) {
            throw new SecurityException("Vous n'avez pas le droit de voir les moyens de paiement de cet utilisateur");
        }

        return payementMethodRepository.findByUser_Id(targetUserId);
    }

}
