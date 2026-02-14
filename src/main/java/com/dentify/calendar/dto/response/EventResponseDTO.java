package com.dentify.calendar.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class EventResponseDTO{

    private String id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long id_appointment;
    private String type;
    private String patient_name;
    private String appointment_status;
    private String notes;
}
