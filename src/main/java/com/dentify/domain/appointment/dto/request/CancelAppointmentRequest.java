package com.dentify.domain.appointment.dto.request;

public record CancelAppointmentRequest(Long id_appointment,
                                       String reason_for_cancellation,
                                       String cancelledBy) {
}
