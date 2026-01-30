package com.floss.odontologia.dto.response.calendar;


import com.floss.odontologia.dto.response.AppointmentResponseDTO;

import java.time.LocalTime;

public record EventDetailResponseDTO(String id,
                                     String type,
                                     LocalTime start_time,
                                     LocalTime final_time,
                                     Integer duration_minutes,
                                     AppointmentResponseDTO appointment) {
}
