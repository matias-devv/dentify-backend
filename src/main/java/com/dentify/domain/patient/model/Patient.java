package com.dentify.domain.patient.model;

import com.dentify.domain.patient.enums.CoverageType;
import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.diagnosis.model.Diagnosis;
import com.dentify.domain.patientstat.model.PatientStat;
import com.dentify.domain.responsibleadult.model.ResponsibleAdult;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity @Getter @Setter @AllArgsConstructor
@NoArgsConstructor
@Table(name = "patients") @Builder
public class Patient {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id_patient;

    @Column(unique = true)
    private String dni;
    private String name;
    private String surname;
    private Integer age;
    private LocalDate date_of_birth;
    private String insurance;

    @Enumerated (EnumType.STRING)
    private CoverageType coverageType;

    private String phone_number;
    private String email;

    @OneToMany (mappedBy = "patient")
    private List<Diagnosis> diagnoses;

    @OneToMany (mappedBy = "patient")
    private List<Appointment> appointments;

    @ManyToMany ( cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable ( name = "patients_responsibles_adults", joinColumns = @JoinColumn ( name = "id_patient"),
                 inverseJoinColumns = @JoinColumn ( name = "id_responsible_adult"))
    private List<ResponsibleAdult> responsibleAdultList;

    @OneToOne( cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    @JoinColumn( name = "id_stat")
    private PatientStat patient_stat;
}
