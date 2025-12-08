package com.floss.odontologia.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity @Getter @Setter
public class Dentist {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id_dentist;

    private String name;
    private String surname;
    private String dni;

    @OneToOne
    @JoinColumn(name="id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name="id_speciality")
    private Speciality speciality;

    @OneToMany(mappedBy = "dentist")
    private List<Schedule> schedulesList;

    @OneToMany(mappedBy = "dentist")
    private List<Appointment> appointmentList;
}
