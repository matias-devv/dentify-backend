package com.dentify.domain.agenda.repository;

import com.dentify.domain.agenda.model.Agenda;
import com.dentify.domain.appointment.model.Appointment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IAgendaRepository extends JpaRepository<Agenda, Long> {

    // Query 1: Traer agenda con schedules
    @EntityGraph(attributePaths = {"schedules"})
    @Query("SELECT a FROM Agenda a WHERE a.id_agenda = :id")
    Optional<Agenda> findAgendaWithSchedules(@Param("id") Long id);

}
