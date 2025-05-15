package com.example.splitz.dto.event;

import java.time.LocalDateTime;
import java.util.List;

import com.example.splitz.dto.user.UserSummaryDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventSummaryDTO {
    private Integer id;
    private List<UserEventSummaryDTO> userEvents;
    private String eventName;
    private LocalDateTime eventDate;
    private String inviteCode;
    private UserSummaryDTO user;
    private LocalDateTime inviteCodeExpiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String organizerUsername;
}
