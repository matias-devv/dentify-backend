package com.dentify.domain.patientstat.model;

import com.dentify.domain.patient.enums.PatientRiskLevel;
import com.dentify.domain.patient.model.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity @AllArgsConstructor @NoArgsConstructor @Getter @Setter @Table ( name = "patient_statistics")
public class PatientStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_stat;

    @OneToOne( mappedBy = "patient_stat")
    private Patient patient;

    private Integer total_appointments;
    private Integer total_confirmed_appointments;
    private Integer total_no_shows;

    private Integer no_shows_last_30_days;
    private Integer no_shows_last_90_days;

    private LocalDateTime last_no_show_at;
    private LocalDateTime last_appointment_at;

    private Integer paid_appointments_count;
    private Integer unpaid_appointments_count;

    private LocalDateTime last_payment_at;

    //Attendance ratio: 0.0 → 1.0
    private double risk_score;

    //He confirmed but didn't attend
    private Integer confirmed_but_no_show_count;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private PatientRiskLevel risk_level;


    public void incrementNoShowCount() {
        this.total_no_shows++;
    }

    public void increment30DaysCount() {
        this.no_shows_last_30_days++;
    }

    public void increment90DaysCount() {
        this.no_shows_last_90_days++;
    }

    public void incrementConfirmedButNoShowCount() {
        this.confirmed_but_no_show_count++;
    }

    public void recalculateRiskScore() {
        int maxThreshold = 10; // The limit where the ratio reaches 1.0

        //I use Math.min so that the ratio does not exceed 1.0 if the patient is 11 or older
        this.risk_score = (double) Math.min( this.total_no_shows, maxThreshold) / maxThreshold;
    }


    public void reset30Days() {
        this.setNo_shows_last_30_days(0);
    }

    public void reset90Days() {
        this.setNo_shows_last_90_days(0);
    }
}
