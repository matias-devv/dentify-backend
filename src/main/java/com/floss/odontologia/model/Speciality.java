package com.floss.odontologia.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter @Entity
public class Speciality {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id_speciality;

    private String name;

    @OneToMany (mappedBy = "speciality")
    private List<Dentist> dentists;
}
