package com.floss.odontologia.dto.response.calendar;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public record WeekSummaryResponseDTO(Long agenda_id,
                                     String agenda_name,
                                     @NotBlank LocalDate startDate,
                                     @NotBlank LocalDate endDate,
                                     @NotBlank List<EventResponseDTO> events) {
}
