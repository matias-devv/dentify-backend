package com.floss.odontologia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity @Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Table( name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_schedule;

    @ManyToOne ( fetch = FetchType.LAZY)
    @JoinColumn( name = "id_agenda",  nullable = false)
    private Agenda agenda;

    private LocalTime start_time;
    private LocalTime end_time;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "schedule_days",
            joinColumns = @JoinColumn(name = "id_schedule")
    )
    @Column(name = "day_of_week")
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> days = new HashSet<>();

    //helper method for days
    public void addDay(DayOfWeek day) {
        days.add(day);
    }

}
