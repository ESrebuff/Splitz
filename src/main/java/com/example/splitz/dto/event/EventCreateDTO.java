package com.example.splitz.dto.event;

import java.time.LocalDateTime;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateDTO {

    @NotBlank(message = "Le nom de l'événement est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom de l'événement doit contenir entre 2 et 100 caractères")
    private String eventName;

    @NotNull(message = "La date de l'événement est obligatoire")
    private LocalDateTime eventDate;
}
