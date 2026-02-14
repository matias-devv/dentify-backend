package com.dentify.calendar.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record WeekDateRangeRequestDTO(Long id_agenda,
                                      @NotBlank LocalDate startDate,
                                      @NotBlank LocalDate endDate) {
}
