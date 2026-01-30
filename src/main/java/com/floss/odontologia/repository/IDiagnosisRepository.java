package com.floss.odontologia.repository;

import com.floss.odontologia.model.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDiagnosisRepository extends JpaRepository<Diagnosis, Long> {
}
