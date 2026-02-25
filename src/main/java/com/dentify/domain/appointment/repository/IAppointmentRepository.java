package com.dentify.domain.appointment.repository;

import com.dentify.domain.appointment.enums.AppointmentStatus;
import com.dentify.domain.appointment.model.Appointment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IAppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDate(LocalDate date);

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

    @Query("SELECT app FROM Appointment app " +
            "JOIN FETCH app.patient " +
            "JOIN FETCH app.app_user " +
            "LEFT JOIN FETCH app.treatment t " +
            "LEFT JOIN FETCH t.product " +
            "WHERE app.agenda.id_agenda = :agendaId " +
            "AND app.date BETWEEN :startDate AND :endDate")
    List<Appointment> findAppointmentsByAgendaAndDateRange(
            @Param("agendaId") Long agendaId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT app FROM Appointment app " +
            "JOIN FETCH app.patient " +
            "LEFT JOIN FETCH app.treatment t " +
            "LEFT JOIN FETCH t.product " +
            "WHERE app.agenda.id_agenda = :agendaId " +
            "AND app.date = :date")
    List<Appointment> findAppointmentsByAgendaAndDate(
            @Param("agendaId") Long agendaId,
            @Param("date") LocalDate date
    );

    @Query("SELECT DISTINCT app FROM Appointment app " +
            "JOIN FETCH app.patient " +
            "JOIN FETCH app.app_user au " +
            "LEFT JOIN FETCH au.specialities " +
            "JOIN FETCH app.agenda " +
            "LEFT JOIN FETCH app.treatment t " +
            "LEFT JOIN FETCH t.product " +
            "LEFT JOIN FETCH app.pays " +
            "WHERE app.id_appointment = :id")
    Optional<Appointment> findByIdWithAllDetails(@Param("id") Long id);

    @Query("SELECT app FROM Appointment app " +
            "JOIN FETCH app.patient " +
            "WHERE app.id_appointment = :id")
    Optional<Appointment> findByIdWithPatient(@Param("id") Long id);

    @Query("""
    SELECT COUNT(a)
    FROM Appointment a
    WHERE a.date = CURRENT_DATE
      AND a.appointmentStatus NOT IN :statuses
    """)
    Long countAppointmentsTodayExcludingStatuses(@Param("statuses") List<AppointmentStatus> statuses);

    @Query("""
    SELECT a
    FROM Appointment a
    JOIN FETCH a.patient p
    WHERE a.date = :date
      AND a.appointmentStatus IN :statuses
      AND a.startTime >= :currentTime
    ORDER BY a.startTime ASC
    """)
    List<Appointment> findNextAppointment(
            @Param("date") LocalDate date,
            @Param("currentTime") LocalTime currentTime,
            @Param("statuses") List<AppointmentStatus> statuses,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "pays")
    @Query("""
       SELECT a
       FROM Appointment a
       WHERE a.id_appointment = :id
       """)
    Optional<Appointment> findByIdWithPays(@Param("id") Long id);
}
