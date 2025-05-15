package com.example.splitz.dto.user;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;
    private boolean consentGiven;
    private LocalDateTime consentGivenAt;
}
