package com.dentify.domain.appointment.model;

import com.dentify.domain.agenda.model.Agenda;
import com.dentify.domain.appointment.enums.AppointmentStatus;
import com.dentify.domain.notification.model.Notification;
import com.dentify.domain.pay.enums.PaymentMethod;
import com.dentify.domain.patient.model.Patient;
import com.dentify.domain.pay.model.Pay;
import com.dentify.domain.treatment.model.Treatment;
import com.dentify.domain.user.model.AppUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Table ( name = "appointments") @Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_appointment;

    @Column(nullable = true)
    private String notes;

    @Column( nullable = true)
    private String patient_instructions;

    @Enumerated( EnumType.STRING)
    private AppointmentStatus appointmentStatus = AppointmentStatus.SCHEDULED;

    @Column( nullable = false)
    private LocalDate date;

    @Column( nullable = false)
    private LocalTime startTime;

    @Column( nullable = false)
    private Integer duration_minutes;

    @Column(nullable = true)
    private String reason_for_cancellation;

    @Column(name = "attendance_confirmed")
    private Boolean attendanceConfirmed = false;

    private LocalDateTime confirmed_at;

    //n appointment -> 1 user
    @ManyToOne
    @JoinColumn( name = "id_app_user", nullable = false)
    private AppUser app_user;

    //n appointment -> 1 patient
    @ManyToOne
    @JoinColumn( name = "id_patient", nullable = false)
    private Patient patient;

    //one appointment -> n pays
    @OneToMany ( mappedBy = "appointment")
    private List<Pay> pays;

    //one appointment -> n notifications
    @OneToMany ( mappedBy = "appointment")
    private List<Notification> notifications;

    //n appointments -> one treatment
    @ManyToOne
    @JoinColumn( name = "id_treatment", nullable = true)
    private Treatment treatment;

    //many appointments -> one agenda
    @ManyToOne
    @JoinColumn( name = "id_agenda", nullable = false)
    private Agenda agenda;

    public PaymentMethod getPrimaryPaymentMethod() {
        return this.pays.stream()
                .findFirst()
                .map(Pay::getPayment_method)
                .orElse(null);
    }

    public Pay getPrimaryPayment() {
        return this.pays.stream()
                .findFirst()
                .orElseThrow( () -> new IllegalStateException("Appointment without payments. appointmentId=" + this.id_appointment) );
    }

}
