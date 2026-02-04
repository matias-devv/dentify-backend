package com.floss.odontologia.repository;

import com.floss.odontologia.enums.TreatmentStatus;
import com.floss.odontologia.model.Patient;
import com.floss.odontologia.model.Product;
import com.floss.odontologia.model.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ITreatmentRepository extends JpaRepository<Treatment, Long> {

    Optional<Treatment> findByPatientAndProductAndTreatmentStatus(Patient patient, Product product,
                                                                  TreatmentStatus treatmentStatus);

}
