package com.floss.odontologia.dto.request.calendar;

import java.time.LocalDate;

public record DayRequestDTO(Long id_agenda,
                            LocalDate date){

}
