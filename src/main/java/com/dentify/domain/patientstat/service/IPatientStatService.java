package com.dentify.domain.patientstat.service;

import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.patient.model.Patient;
import com.dentify.domain.patientstat.model.PatientStat;

public interface IPatientStatService {

   PatientStat findPatientStatById(Long id);

   PatientStat actualizeStatsFromPatient(Appointment appointment, Patient patient);

    PatientStat createPatientStat();
}
