package com.dentify.domain.appointment.dto;

import com.dentify.domain.appointment.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record FullAppointmentResponse(Long id_appointment,
                                      AppointmentStatus status,
                                      LocalTime startTime,
                                      LocalTime endTime,
                                      Integer duration,
                                      Boolean attendanceConfirmed,
                                      LocalDateTime confirmed_at,
                                      PatientResponse patient,
                                      ProductResponse product,
                                      AppUserResponse dentist,
                                      AgendaResponse agenda,
                                      TreatmentResponse treatment,
                                      PayResponse pay,
                                      String notes,
                                      String patient_instructions,
                                      String reason_for_cancellation) {
}
