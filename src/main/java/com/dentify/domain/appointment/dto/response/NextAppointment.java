package com.dentify.domain.appointment.dto.response;

import java.time.LocalTime;

public record NextAppointment(LocalTime time,
                              String patient_name,
                              String patient_surname) {
}
