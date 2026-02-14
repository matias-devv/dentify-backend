package com.dentify.domain.patientstat.repository;

import com.dentify.domain.patientstat.model.PatientStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPatientStatRepository extends JpaRepository<PatientStat, Long> {
}
