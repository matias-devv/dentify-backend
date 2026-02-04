package com.floss.odontologia.dto.response.appointment;

import com.floss.odontologia.enums.AppointmentStatus;
import com.floss.odontologia.enums.PaymentMethod;
import com.floss.odontologia.enums.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record CreateAppointmentResponseDTO(Long id_appointment,
                                           Long id_treatment,
                                           Long id_pay,

                                           // Appointment info
                                           LocalDate date,
                                           LocalTime start_time,
                                           Integer duration_minutes,
                                           LocalTime end_time,

                                           // Payment info
                                           BigDecimal amount_to_pay,
                                           PaymentMethod payment_method,
                                           String payment_link,

                                           // Status
                                           AppointmentStatus appointment_status,
                                           PaymentStatus payment_status,

                                           // Product info
                                           String product_name) {
}
