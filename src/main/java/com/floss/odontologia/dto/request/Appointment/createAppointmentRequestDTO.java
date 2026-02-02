package com.floss.odontologia.dto.request.Appointment;

import com.floss.odontologia.enums.PaymentMethod;

import java.time.LocalDate;
import java.time.LocalTime;

public record createAppointmentRequestDTO(Long id_patient,
                                          Long id_product,
                                          LocalDate date,
                                          LocalTime start_time,
                                          Integer duration_minutes,
                                          PaymentMethod paymentMethod ) {
}
