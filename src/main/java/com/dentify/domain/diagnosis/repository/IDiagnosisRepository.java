package com.dentify.domain.diagnosis.repository;

import com.dentify.domain.diagnosis.model.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDiagnosisRepository extends JpaRepository<Diagnosis, Long> {
}
