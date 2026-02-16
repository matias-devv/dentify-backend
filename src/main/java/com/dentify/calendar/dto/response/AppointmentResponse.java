package com.dentify.calendar.dto.response;

import com.dentify.domain.appointment.enums.AppointmentStatus;

public record AppointmentResponse(Long id,
                                  String patient_name,
                                  String patient_surname,
                                  AppointmentStatus status,
                                  String productName) {
}
