package com.dentify.domain.treatment.service;

import com.dentify.domain.treatment.enums.TreatmentStatus;
import com.dentify.domain.patient.model.Patient;
import com.dentify.domain.pay.model.Pay;
import com.dentify.domain.product.model.Product;
import com.dentify.domain.treatment.model.Treatment;
import com.dentify.domain.treatment.repository.ITreatmentRepository;
import com.dentify.domain.user.model.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class TreatmentService implements ITreatmentService {

    private final ITreatmentRepository treatmentRepository;

    @Override
    public void actualizeTreatmentOutstandingBalanceAndSetStatusInProgress(Treatment treatment, Pay pay) {
        BigDecimal updatedAmount = treatment.getOutstanding_balance().subtract( pay.getAmount() );
        //set new outstanding balance
        treatment.setOutstanding_balance(updatedAmount);

        //actualize treatment status( CREATED) ->  IN PROGRESS
        if (treatment.getTreatmentStatus() == TreatmentStatus.CREATED) {
            treatment.setTreatmentStatus(TreatmentStatus.IN_PROGRESS);
        }
        treatmentRepository.save(treatment);
    }

    @Override
    public Treatment findOrCreateTreatment(Patient patient, Product product, AppUser dentist) {

        // Find active treatment for this patient and service
        Optional<Treatment> existingTreatment = treatmentRepository.findByPatientAndProductAndTreatmentStatus( patient,
                                                                                                               product,
                                                                                                               TreatmentStatus.CREATED);

        if ( existingTreatment.isPresent() ) {
            return existingTreatment.get();
        }

        // if not exists -> new treatment
        Treatment newTreatment = this.buildTreatment( patient, product, dentist);

        treatmentRepository.save(newTreatment);

        return newTreatment;
    }

    @Override
    public void calculateOutstandingBalanceAfterRefund(Treatment treatment, Pay pay) {
        //Return balance to treatment
        BigDecimal newBalance = treatment.getOutstanding_balance().add(pay.getAmount());

        treatment.setOutstanding_balance(newBalance);

        treatmentRepository.save(treatment);
    }

    private Treatment buildTreatment(Patient patient, Product product, AppUser dentist) {
        return Treatment.builder()
                        .base_price( product.getUnit_price())
                        .discount(BigDecimal.ZERO)
                        .final_price( product.getUnit_price())
                        .outstanding_balance( product.getUnit_price())
                        .treatmentStatus( TreatmentStatus.CREATED)
                        .start_date( LocalDateTime.now())
                        .app_user(dentist)
                        .patient(patient)
                        .product(product)
                        .build();
    }
}
