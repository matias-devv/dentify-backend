package com.floss.odontologia.dto.response.Appointment;

import com.floss.odontologia.enums.AppointmentStatus;
import lombok.Builder;

@Builder
public record createAppointmentResponseDTO(Long id_appointment,
                                           Long id_treatment,
                                           Long id_pay,
                                           String pay_link, //only if PaymentMethod = MERCADO_PAGO
                                           AppointmentStatus appointment_status) {
}
