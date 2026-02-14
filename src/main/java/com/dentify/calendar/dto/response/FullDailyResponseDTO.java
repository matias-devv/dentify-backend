package com.dentify.calendar.dto.response;

import java.time.LocalDate;
import java.util.List;

public record FullDailyResponseDTO(Long id_agenda,
                                   String agenda_name,
                                   LocalDate date,
                                   FastRecap dayRecap,
                                   List<EventDetailResponseDTO> slots) {
}
