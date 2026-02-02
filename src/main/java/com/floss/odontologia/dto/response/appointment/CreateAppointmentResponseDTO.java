package com.floss.odontologia.dto.response.appointment;

import com.floss.odontologia.enums.AppointmentStatus;
import lombok.Builder;

@Builder
public record CreateAppointmentResponseDTO(Long id_appointment,
                                           Long id_treatment,
                                           Long id_pay,
                                           String pay_link, //only if PaymentMethod = MERCADO_PAGO
                                           AppointmentStatus appointment_status) {
}
