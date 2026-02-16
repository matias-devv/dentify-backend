package com.dentify.domain.appointment.repository;

import com.dentify.domain.appointment.enums.AppointmentStatus;
import com.dentify.domain.appointment.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface IAppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByAppointmentStatusAndDateBetween(AppointmentStatus appointmentStatus, LocalDate dateStart, LocalDate dateEnd);

    List<Appointment> findByDateBeforeAndAppointmentStatusIn(LocalDate date, List<AppointmentStatus> appointmentStatus);

    List<Appointment> findByDate(LocalDate date);

    List<Appointment> findByDateAndAppointmentStatus(LocalDate date, AppointmentStatus appointmentStatus);

    List<Appointment> findByStartTimeBetween(LocalTime start, LocalTime end);

    List<Appointment> findByAppointmentStatusAndAttendanceConfirmedAndStartTimeBetween(
                                                                                        AppointmentStatus appointmentStatus,
                                                                                        Boolean attendanceConfirmed,
                                                                                        LocalDateTime startTimeStart,
                                                                                        LocalDateTime startTimeEnd );

    @Query("""
                SELECT a
                FROM Appointment a
                WHERE a.appointmentStatus = :status
                  AND a.attendanceConfirmed = :confirmed
                  AND a.date BETWEEN :startDate AND :endDate
                  AND a.startTime BETWEEN :startTime AND :endTime
            """)
    List<Appointment> findAppointmentsInRange(
            @Param("status") AppointmentStatus status,
            @Param("confirmed") boolean confirmed,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    @Query("SELECT a FROM Appointment a WHERE a.date <= :date AND a.appointmentStatus IN :statuses")
    List<Appointment> findByDateLessThanEqualAndAppointmentStatusIn(@Param("date") LocalDate date, @Param("statuses") List<AppointmentStatus> statuses );

    Appointment findByDateAndStartTime(LocalDate date, LocalTime startTime);

    // Query 2: Traer solo los appointments filtrados
    @Query("SELECT app FROM Appointment app " +
            "JOIN FETCH app.patient " +
            "WHERE app.agenda.id_agenda = :agendaId " +
            "AND app.date BETWEEN :startDate AND :endDate")
    List<Appointment> findAppointmentsByAgendaAndDateRange(
            @Param("agendaId") Long agendaId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
