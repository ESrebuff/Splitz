package com.example.splitz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserInfoDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Boolean consentGiven;

}
