package com.dentify.domain.treatment.service;

import com.dentify.domain.patient.model.Patient;
import com.dentify.domain.pay.model.Pay;
import com.dentify.domain.product.model.Product;
import com.dentify.domain.treatment.model.Treatment;
import com.dentify.domain.user.model.AppUser;

public interface ITreatmentService {

    void actualizeTreatmentOutstandingBalanceAndSetStatusInProgress(Treatment treatment, Pay pay);

    public Treatment findOrCreateTreatment(Patient patient, Product product, AppUser dentist);

    void calculateOutstandingBalanceAfterRefund(Treatment treatment, Pay pay);
}
