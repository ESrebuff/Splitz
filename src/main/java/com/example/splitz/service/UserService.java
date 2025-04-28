package com.example.splitz.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

}

// TODO : pouvoir se connecter
// TODO : pouvoir se connecter avec google
// TODO : pouvoir se connecter avec facebook
// TODO : pouvoir se connecter avec apple
// TODO : pouvoir se connecter avec linkedin
// TODO : pouvoir se déconnecter
// TODO : pouvoir demander à supprimer son compte
// TODO : pouvoir changer son mot de passe
// TODO : pouvoir changer son numéro de téléphone
// TODO : pouvoir changer son nom
// TODO : pouvoir changer son prénom
// TODO : pouvoir accepter les CGU et la politique de confidentialité
// TODO : pouvoir refuser les CGU et la politique de confidentialité

// TODO : à revoir pour ajouter un service

// TODO : ajouter les moyens de paiement
// TODO : ajouter les dépenses