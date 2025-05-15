package com.example.splitz.dto.event;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDTO {

    private Integer id;
    private List<UserEventSummaryDTO> userEvents;
    private String eventName;
    private LocalDateTime eventDate;
    private String inviteCode;
    private LocalDateTime inviteCodeExpiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String organizerUsername;

}
