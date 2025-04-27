package com.example.splitz.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventGetDTO {

    private Integer id;
    private String eventName;
    private LocalDateTime eventDate;
    private String inviteCode;
    private LocalDateTime inviteCodeExpiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String organizerUsername;

}
