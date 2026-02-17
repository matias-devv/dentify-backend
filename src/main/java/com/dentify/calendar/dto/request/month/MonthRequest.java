package com.dentify.calendar.dto.request.month;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record MonthRequest(@NotBlank Long id_agenda,
                           @NotBlank Integer year,
                           @NotBlank Integer month_number,
                           Long id_product) {
}
