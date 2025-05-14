package com.example.splitz.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.splitz.dto.UserUpdatePasswordDTO;
import com.example.splitz.dto.UserUpdateInfoDTO;
import com.example.splitz.dto.UserCreateDTO;
import com.example.splitz.model.User;
import com.example.splitz.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(UserCreateDTO userCreateDTO) {
        User user = new User();
        user.setFirstName(userCreateDTO.getFirstName());
        user.setLastName(userCreateDTO.getLastName());
        user.setPhoneNumber(userCreateDTO.getPhoneNumber());
        user.setUsername(userCreateDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> authenticate(String username, String rawPassword) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public void markAccountForDeletion(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setDataDeletedAt(LocalDateTime.now().plusDays(30));
            userRepository.save(user);
        });
    }

    public void updateUserInfo(String username, UserUpdateInfoDTO dto) {
        userRepository.findByUsername(username).ifPresent(user -> {
            if (dto.getFirstName() != null)
                user.setFirstName(dto.getFirstName());
            if (dto.getLastName() != null)
                user.setLastName(dto.getLastName());
            if (dto.getPhoneNumber() != null)
                user.setPhoneNumber(dto.getPhoneNumber());

            if (dto.getConsentGiven() != null) {
                if (!user.isConsentGiven() && dto.getConsentGiven()) {
                    user.setConsentGivenAt(LocalDateTime.now());
                }
                user.setConsentGiven(dto.getConsentGiven());
            }

            userRepository.save(user);
        });
    }

    public boolean updatePassword(String username, UserUpdatePasswordDTO dto) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

}

// TODO : pouvoir se connecter avec google
// TODO : pouvoir se connecter avec facebook
// TODO : pouvoir se connecter avec apple
// TODO : pouvoir se connecter avec linkedin
// TODO : demander un lien de modification de mot de passe

// TODO : à revoir pour ajouter un service

// TODO : ajouter les dépenses