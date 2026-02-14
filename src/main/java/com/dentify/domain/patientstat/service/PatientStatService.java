package com.dentify.domain.patientstat.service;

import com.dentify.domain.patient.enums.PatientRiskLevel;
import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.patient.model.Patient;
import com.dentify.domain.patientstat.model.PatientStat;
import com.dentify.domain.patientstat.repository.IPatientStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
public class PatientStatService implements IPatientStatService {

    private final IPatientStatRepository statRepository;

    @Override
    public PatientStat findPatientStatById(Long id) {
        return statRepository.findById(id).orElse(null);
    }

    @Override
    public PatientStat actualizeStatsFromPatient(Appointment appointment, Patient patient) {

        PatientStat patientStat = this.findPatientStatById( appointment.getPatient().getId_patient() );

        this.normalizeCounters( patientStat);

        patientStat.setLast_no_show_at( appointment.getDate().atTime( appointment.getStartTime() ) );
        patientStat.setLast_appointment_at( appointment.getDate().atTime( appointment.getStartTime() ) );
        patientStat.setUpdatedAt( LocalDateTime.now() );

        this.categorizeRecentNoShow( patientStat, appointment.getDate().atTime( appointment.getStartTime()) );

        if( appointment.getAttendanceConfirmed() == true ){
            patientStat.incrementConfirmedButNoShowCount();
        }
        patientStat.incrementNoShowCount();

        patientStat.recalculateRiskScore();
        this.calculatePatientRiskLevel(patientStat);

        statRepository.save(patientStat);

        return patientStat;
    }

    @Override
    public PatientStat createPatientStat() {
        PatientStat stat = new PatientStat();

        return statRepository.save(stat);
    }

    private void categorizeRecentNoShow(PatientStat patientStat, LocalDateTime appointmentDateTime ) {

        LocalDate now = LocalDate.now();

        long days = DAYS.between( appointmentDateTime.toLocalDate(), now);

        if ( days <= 30) {

            patientStat.increment30DaysCount();

        }

        if ( days <= 90){

            patientStat.increment90DaysCount();
        }
    }

    private void calculatePatientRiskLevel(PatientStat stat) {

        double ratio = stat.getRisk_score();

        //If the ratio is less than 10% -> low risk (reliable patient)
        if (stat.getNo_shows_last_30_days() >= 3 || ratio >= 0.4) {
            stat.setRisk_level(PatientRiskLevel.CRITICAL);
            return;
        }

        if (stat.getNo_shows_last_90_days() >= 2 || ratio >= 0.25) {
            stat.setRisk_level(PatientRiskLevel.HIGH);
            return;
        }

        if (stat.getNo_shows_last_30_days() >= 1 || ratio >= 0.1) {
            stat.setRisk_level(PatientRiskLevel.MEDIUM);
            return;
        }

        stat.setRisk_level(PatientRiskLevel.LOW);
    }

    private void normalizeCounters(PatientStat stat) {

        if ( stat.getLast_no_show_at() == null) {
            return; //first no show
        }

        LocalDate now = LocalDate.now();

        long days = DAYS.between(stat.getLast_no_show_at().toLocalDate(), now);

        if (days > 90) {
            stat.reset30Days();
            stat.reset90Days();

        } else if (days > 30) {
            stat.reset30Days();
        }
    }

}
