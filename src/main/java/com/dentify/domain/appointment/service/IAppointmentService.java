package com.dentify.domain.appointment.service;

import com.dentify.domain.appointment.dto.CreateAppointmentRequestDTO;
import com.dentify.domain.appointment.dto.CreateAppointmentResponseDTO;
import com.dentify.domain.appointment.enums.AppointmentStatus;
import com.dentify.domain.appointment.model.Appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface IAppointmentService {

    //read
    public Appointment getAppointmentById(Long id);

    public CreateAppointmentResponseDTO saveAppointmentWithPay(CreateAppointmentRequestDTO request);

    public void actualizeAppointmentStatusToConfirmed(Appointment appointment);

    public void cancelAppointment( AppointmentStatus typeOfCancellation, Appointment appointment, String message);

    public List<Appointment> findByDateBeforeAndAppointmentStatusIn(LocalDate targetDate, List<AppointmentStatus> statuses);

    public void markNoShow(Appointment appointment);

    public List<Appointment> findByAppointmentStatusAndDate(LocalDate targetDate, AppointmentStatus appointmentStatus);

    List<Appointment> findByAppointmentStatusAndDateBetween(AppointmentStatus status, LocalDate startDate, LocalDate finalDate);

    List<Appointment> findScheduledAppointmentsBetween(LocalTime startTime, LocalTime finalTime);

    List<Appointment> findReservedAppointmentsNotConfirmed(LocalDateTime startDate, LocalDateTime finalDate);

    List<Appointment> findReservedAppointmentsConfirmed(LocalTime startTime, LocalTime finalTime);

    List<Appointment> findByDateLessThanEqualAndAppointmentStatusIn(LocalDate today, List<AppointmentStatus> scheduled);
}
