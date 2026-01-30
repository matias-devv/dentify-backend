package com.floss.odontologia.dto.response.calendar;

import com.floss.odontologia.model.Schedule;

import java.time.LocalDate;
import java.util.List;

public record AgendaResponseDTO(Long id_agenda,
                                String agenda_name,
                                Boolean active,
                                LocalDate start_date,
                                LocalDate final_date,
                                Long id_product,
                                String product_name,
                                List<Schedule> schedules) {
}
