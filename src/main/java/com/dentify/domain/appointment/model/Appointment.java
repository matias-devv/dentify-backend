package com.dentify.domain.appointment.model;

import com.dentify.domain.agenda.model.Agenda;
import com.dentify.domain.appointment.enums.AppointmentStatus;
import com.dentify.domain.dentist.Dentist;
import com.dentify.domain.notification.model.Notification;
import com.dentify.domain.pay.enums.PaymentMethod;
import com.dentify.domain.patient.model.Patient;
import com.dentify.domain.pay.model.Pay;
import com.dentify.domain.treatment.model.Treatment;
import com.dentify.domain.userProfile.model.UserProfile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    private LocalDateTime cancelled_at;

    // N:1 — The appointment belongs to a specific dentist
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dentist_id", nullable = false)
    private Dentist dentist;

    // N:1 — siempre tiene un paciente
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    //one appointment -> n pays
    @OneToMany ( mappedBy = "appointment")
    private List<Pay> pays;

    //one appointment -> n notifications
    @OneToMany ( mappedBy = "appointment")
    private List<Notification> notifications;

    // N:1 — el turno pertenece a una agenda
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agenda_id", nullable = false)
    private Agenda agenda;

    // NULLABLE — un turno puede existir sin tratamiento (consulta suelta)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treatment_id")
    private Treatment treatment;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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

    public boolean isCancelled() {
        if ( this.getAppointmentStatus().equals( AppointmentStatus.CANCELLED_BY_SYSTEM)) return true;
        if ( this.getAppointmentStatus().equals( AppointmentStatus.CANCELLED_BY_DENTIST)) return true;
        if ( this.getAppointmentStatus().equals( AppointmentStatus.CANCELLED_BY_SECRETARY)) return true;
        if ( this.getAppointmentStatus().equals( AppointmentStatus.CANCELLED_BY_PATIENT)) return true;
        return false;
    }
}
