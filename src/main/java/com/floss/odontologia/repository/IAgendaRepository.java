package com.floss.odontologia.repository;

import com.floss.odontologia.model.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAgendaRepository extends JpaRepository<Agenda, Long> {
}
