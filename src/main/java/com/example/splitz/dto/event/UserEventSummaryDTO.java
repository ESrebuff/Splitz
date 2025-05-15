package com.example.splitz.dto.event;

import com.example.splitz.dto.user.UserSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEventSummaryDTO {
    private UserSummaryDTO user;
    private String role;
}