package com.dentify.calendar.dto.request.day;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record DetailedDayRequest(@NotBlank Long id_agenda,
                                 Long id_product,
                                 @NotBlank @Future LocalDate startDate) {
}
