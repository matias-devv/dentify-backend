package com.dentify.domain.agenda.model;

import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.schedule.model.Schedule;
import com.dentify.domain.product.model.Product;
import com.dentify.domain.user.model.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

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

    @Column( name = "duration_minutes", nullable = false)
    private Integer duration_minutes;

    // n agendas -> 1 product
    @ManyToOne( fetch =  FetchType.LAZY)
    @JoinColumn ( name = "id_product", nullable = true)
    private Product product;

    // 1 agenda -> n schedules
    @OneToMany ( mappedBy = "agenda", cascade = CascadeType.ALL)
    private Set<Schedule> schedules = new HashSet<>();

    // 1 agenda -> n appointments
    @OneToMany ( mappedBy = "agenda")
    private List<Appointment> appointments;

    //helper method para bidirectional sync
    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
        schedule.setAgenda(this);
    }

    public void removeHorario(Schedule schedule) {
        schedules.remove(schedule);
        schedule.setAgenda(null);
    }

    public Map<DayOfWeek, List<Schedule>> fillMapDays() {

        Map<DayOfWeek, List<Schedule>> mapDays = new HashMap<>();

        this.schedules.forEach(schedule -> {
                                                        schedule.getDays().forEach(day -> {
                                                            mapDays
                                                                    .computeIfAbsent(day, d -> new ArrayList<>())
                                                                    .add(schedule);
                                                        });
        });

        return mapDays;
    }
}
