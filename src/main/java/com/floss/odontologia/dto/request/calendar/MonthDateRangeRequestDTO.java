package com.floss.odontologia.dto.request.calendar;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record MonthDateRangeRequestDTO(Long id_agenda,
                                       @NotBlank LocalDate start_date,
                                       @NotBlank LocalDate final_date) {
}
