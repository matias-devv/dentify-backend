package com.floss.odontologia.model;

import com.floss.odontologia.enums.CoverageType;
import jakarta.persistence.*;
import jdk.jshell.Diag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity @Getter @Setter @AllArgsConstructor
@NoArgsConstructor
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id_patient;

    @Column(unique = true)
    private String dni;
    private String name;
    private String surname;
    private String age;
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

}
