package com.example.splitz.mapper;

import org.springframework.stereotype.Component;

import com.example.splitz.dto.EventGetDTO;
import com.example.splitz.model.Event;

@Component
public class EventMapper {

    public static EventGetDTO toDTO(Event event) {
        return EventGetDTO.builder()
                .id(event.getId())
                .eventName(event.getEventName())
                .eventDate(event.getEventDate())
                .inviteCode(event.getInviteCode())
                .inviteCodeExpiresAt(event.getInviteCodeExpiresAt())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .organizerUsername(event.getUser().getUsername())
                .build();
    }
}
