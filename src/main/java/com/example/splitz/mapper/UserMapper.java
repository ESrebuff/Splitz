package com.example.splitz.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.splitz.dto.user.UserCreateDTO;
import com.example.splitz.model.User;

public class UserMapper {
    public static User fromCreateDTO(UserCreateDTO dto, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return user;
    }

}
