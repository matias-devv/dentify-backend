package com.dentify.domain.patient.repository;

import com.dentify.domain.patient.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPatientRepository extends JpaRepository<Patient,Long> {
    Optional<Patient> findByDni(String dni);
}
