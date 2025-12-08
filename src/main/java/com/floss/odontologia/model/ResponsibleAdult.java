package com.floss.odontologia.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
public class ResponsibleAdult {

    @Id
    @GeneratedValue (strategy = GenerationType.SEQUENCE)
    private Long id_responsible;

    private String name;
    private String surname;
    private String dni;
    private String phone_number;

    @ManyToOne
    @JoinColumn(name="id_patient")
    private Patient patient;
}
