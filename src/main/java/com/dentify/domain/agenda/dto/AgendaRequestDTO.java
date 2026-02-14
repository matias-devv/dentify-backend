package com.dentify.domain.agenda.dto;

import com.dentify.domain.schedule.model.Schedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record AgendaRequestDTO(Long id_agenda,
                               @NotNull String agendaName,
                               @NotBlank LocalDate startDate,
                               @NotBlank LocalDate finalDate,
                               @NotBlank Integer duration_minutes,
                               Boolean active,
                               @NotBlank Long idUserApp,
                               Long idProduct,
                               @NotBlank List<Schedule> schedules) {
}
