package com.floss.odontologia.repository;

import com.floss.odontologia.model.PatientStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPatientStatRepository extends JpaRepository<PatientStat, Long> {
}
