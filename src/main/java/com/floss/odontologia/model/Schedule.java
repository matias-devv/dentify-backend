package com.floss.odontologia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Entity @Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Table( name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_schedule;

    @ManyToOne ( fetch = FetchType.LAZY)
    @JoinColumn( name = "id_agenda",  nullable = false)
    private Agenda agenda;

    @Column( name = "duration_minutes", nullable = false)
    private Integer duration_minutes;
    private LocalTime start_time;
    private LocalTime end_time;

    @OneToMany(mappedBy = "horario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Day> days;
}
