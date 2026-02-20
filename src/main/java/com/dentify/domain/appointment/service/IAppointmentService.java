package com.dentify.domain.appointment.service;

import com.dentify.calendar.dto.response.FullAppointmentResponse;
import com.dentify.domain.appointment.dto.request.CancelAppointmentRequest;
import com.dentify.domain.appointment.dto.request.CreateAppointmentRequestDTO;
import com.dentify.domain.appointment.dto.response.AppointmentCancelledResponse;
import com.dentify.domain.appointment.dto.response.CreateAppointmentResponseDTO;
import com.dentify.domain.appointment.enums.AppointmentStatus;
import com.dentify.domain.appointment.model.Appointment;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface IAppointmentService {

    //read
    public FullAppointmentResponse getAppointmentById(Long id);

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

    Appointment findForTheAppointmentOnTheMapByDateAndTime(Map<LocalDateTime, Appointment> mapAppointments, LocalDateTime requestedTimeAndDate);

    Map<LocalDateTime,Appointment> fillInAppointmentMap(List<Appointment> listAppointments);

    List<Appointment> findAppointmentsByAgendaAndDateRange(Long idAgenda, @NotBlank LocalDate startDate, @NotBlank LocalDate endDate);

    List<Appointment> findAppointmentsByAgendaAndDate(  Long agendaId, LocalDate date);

    public AppointmentCancelledResponse cancelAppointment(CancelAppointmentRequest request);
}
