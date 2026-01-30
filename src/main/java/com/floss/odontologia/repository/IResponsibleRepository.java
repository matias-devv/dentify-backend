package com.floss.odontologia.repository;

import com.floss.odontologia.model.ResponsibleAdult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IResponsibleRepository extends JpaRepository<ResponsibleAdult, Long> {
}
