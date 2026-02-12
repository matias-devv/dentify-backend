package com.floss.odontologia.service.interfaces;

import com.floss.odontologia.model.*;

public interface ITreatmentService {

    void actualizeTreatmentOutstandingBalanceAndSetStatusInProgress(Treatment treatment, Pay pay);

    public Treatment findOrCreateTreatment(Patient patient, Product product, AppUser dentist);

    void calculateOutstandingBalanceAfterRefund(Treatment treatment, Pay pay);
}
