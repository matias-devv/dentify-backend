package com.dentify.domain.treatment.repository;

import com.dentify.domain.treatment.enums.TreatmentStatus;
import com.dentify.domain.patient.model.Patient;
import com.dentify.domain.product.model.Product;
import com.dentify.domain.treatment.model.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ITreatmentRepository extends JpaRepository<Treatment, Long> {

    Optional<Treatment> findByPatientAndProductAndTreatmentStatus(Patient patient, Product product,
                                                                  TreatmentStatus treatmentStatus);

}
