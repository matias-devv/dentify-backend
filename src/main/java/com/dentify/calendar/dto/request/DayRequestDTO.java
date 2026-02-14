package com.dentify.calendar.dto.request;

import java.time.LocalDate;

public record DayRequestDTO(Long id_agenda,
                            LocalDate date){

}
