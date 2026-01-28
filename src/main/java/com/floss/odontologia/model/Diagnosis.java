package com.floss.odontologia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "diagnoses")
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_diagnosis;

    @Column(nullable = false)
    private String condition;

    @Column(nullable = false)
    private LocalDate diagnosis_date;

    @Column(nullable = false)
    private String treatment_type;

    @Column(length = 1000)
    private String dentistNotes;

    // n diagnoses -> one patient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_patient", nullable = false)
    private Patient patient;
}
