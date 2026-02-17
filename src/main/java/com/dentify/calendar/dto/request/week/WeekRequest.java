package com.dentify.calendar.dto.request.week;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record WeekRequest(@NotBlank Long id_agenda,
                          Long id_product,
                          @NotBlank @Future LocalDate startDate,
                          @NotBlank @Future LocalDate endDate) {
}
