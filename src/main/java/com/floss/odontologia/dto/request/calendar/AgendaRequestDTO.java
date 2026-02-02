package com.floss.odontologia.dto.request.calendar;

import com.floss.odontologia.model.Schedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record AgendaRequestDTO(Long id_agenda,
                               @NotNull String agendaName,
                               @NotBlank LocalDate startDate,
                               @NotBlank LocalDate finalDate,
                               Boolean active,
                               @NotBlank Long idUserApp,
                               Long idProduct,
                               @NotBlank List<Schedule> schedules) {
}
