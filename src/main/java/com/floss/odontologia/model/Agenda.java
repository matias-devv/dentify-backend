package com.floss.odontologia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity @Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Table ( name  = "agendas")
public class Agenda {

    @Id @GeneratedValue ( strategy = GenerationType.IDENTITY)
    private Long id_agenda;

    // n agendas -> 1 user
    @ManyToOne ( fetch =  FetchType.LAZY)
    @JoinColumn ( name = "id_user")
    private AppUser app_user;

    private String agenda_name;
    private Boolean active;

    @Column (nullable = false)
    private LocalDate start_date;

    @Column (nullable = false)
    private LocalDate final_date;

    // n agendas -> 1 product
    @ManyToOne( fetch =  FetchType.LAZY)
    @JoinColumn ( name = "id_product", nullable = true)
    private Product product;

    // 1 agenda -> n schedules
    @OneToMany ( mappedBy = "agenda", cascade = CascadeType.ALL)
    private List<Schedule> schedules;

    // 1 agenda -> n appointments
    @OneToMany ( mappedBy = "agenda")
    private List<Appointment> appointments;
}
