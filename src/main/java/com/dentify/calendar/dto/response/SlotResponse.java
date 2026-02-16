package com.dentify.calendar.dto.response;


import java.time.LocalTime;

public record SlotResponse(LocalTime startTime,
                           LocalTime endTime,
                           boolean availability,
                           AppointmentResponse appointment) {
}
