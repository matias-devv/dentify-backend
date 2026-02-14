package com.dentify.domain.responsibleadult.repository;

import com.dentify.domain.responsibleadult.model.ResponsibleAdult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IResponsibleRepository extends JpaRepository<ResponsibleAdult, Long> {
}
