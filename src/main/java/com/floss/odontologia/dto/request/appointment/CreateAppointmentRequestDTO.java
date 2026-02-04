package com.floss.odontologia.dto.request.appointment;

import com.floss.odontologia.enums.PaymentMethod;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateAppointmentRequestDTO(@NotBlank(message = "The patient is mandatory")
                                          Long id_patient,

                                          @NotBlank(message = "The dentist is mandatory")
                                          Long id_dentist,

                                          @NotBlank(message = "The agenda is mandatory")
                                          Long id_agenda,

                                          @NotBlank(message = "The product is mandatory")
                                          Long id_product,

                                          @NotBlank(message = "The date is mandatory")
                                          @Future(message = "The date must be in the future.")
                                          LocalDate date,

                                          @NotBlank(message = "The start time is mandatory")
                                          LocalTime start_time,

                                          @NotBlank(message = "The duration in minutes is mandatory")
                                          Integer duration_minutes,

                                          @NotBlank(message = "The payment method is required")
                                          PaymentMethod paymentMethod,

                                          Boolean payNow,

                                          //optional fields
                                          String notes,
                                          String patient_instructions){
}
