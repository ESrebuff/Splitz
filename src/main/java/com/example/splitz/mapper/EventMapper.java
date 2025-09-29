package com.example.splitz.mapper;

import org.springframework.stereotype.Component;

import com.example.splitz.dto.event.EventCreateDTO;
import com.example.splitz.dto.event.EventResponseDTO;
import com.example.splitz.dto.event.EventSummaryDTO;
import com.example.splitz.model.Event;

@Component
public class EventMapper {

    public static EventResponseDTO toDTO(Event event) {
        return EventResponseDTO.builder()
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

    public static Event fromCreateDTO(EventCreateDTO dto) {
        Event event = new Event();
        event.setEventName(dto.getEventName());
        event.setEventDate(dto.getEventDate());
        return event;
    }

    public static EventSummaryDTO toEventSummaryDTO(Event event) {
        return EventSummaryDTO.builder()
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
