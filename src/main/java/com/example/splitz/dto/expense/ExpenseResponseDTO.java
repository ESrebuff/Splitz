package com.example.splitz.dto.expense;

import com.example.splitz.dto.event.EventSummaryDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseResponseDTO {
    private Integer id;
    private EventSummaryDTO event;
    private Integer amount;
    private String description;
    private Integer paidByUserid;
    private Integer eventId;
    private Integer potId;
}
