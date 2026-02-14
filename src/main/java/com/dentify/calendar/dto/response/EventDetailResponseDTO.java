package com.dentify.calendar.dto.response;


import com.dentify.domain.appointment.dto.AppointmentResponseDTO;

import java.time.LocalTime;

public record EventDetailResponseDTO(String id,
                                     String type,
                                     LocalTime start_time,
                                     LocalTime final_time,
                                     AppointmentResponseDTO appointment) {
}
