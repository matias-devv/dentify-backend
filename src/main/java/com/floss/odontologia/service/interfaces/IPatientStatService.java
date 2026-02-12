package com.floss.odontologia.service.interfaces;

import com.floss.odontologia.model.Appointment;
import com.floss.odontologia.model.Patient;
import com.floss.odontologia.model.PatientStat;

import java.util.Optional;

public interface IPatientStatService {

   PatientStat findPatientStatById(Long id);

   PatientStat actualizeStatsFromPatient(Appointment appointment, Patient patient);

    PatientStat createPatientStat();
}
