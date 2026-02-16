package com.dentify.calendar.dto.response;

import com.dentify.calendar.dto.response.week.DayResponse;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public record WeekResponse(Long id_agenda,
                           String agenda_name,
                           @NotBlank LocalDate startDate,
                           @NotBlank LocalDate endDate,
                           @NotBlank List<DayResponse> days) {

}
