package com.floss.odontologia.repository;

import com.floss.odontologia.model.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITreatmentRepository extends JpaRepository<Treatment, Long> {
}
