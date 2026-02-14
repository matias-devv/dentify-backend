package com.dentify.domain.appointment.dto;

import com.dentify.domain.appointment.enums.AppointmentStatus;

public record AppointmentResponseDTO(Long id_appointment,
                                     Long id_patient,
                                     String patient_name,
                                     String patient_phone_number,
                                     String patient_email,
                                     AppointmentStatus status,
                                     String notes) {
}
