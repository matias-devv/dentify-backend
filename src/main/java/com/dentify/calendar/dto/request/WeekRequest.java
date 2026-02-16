package com.dentify.calendar.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record WeekRequest(@NotBlank Long id_agenda,
                          Long id_product,
                          @NotBlank LocalDate startDate,
                          @NotBlank LocalDate endDate) {
}
