package com.dentify.domain.notification.model;

import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.notification.enums.NotificationChannel;
import com.dentify.domain.notification.enums.NotificationStatus;
import com.dentify.domain.notification.enums.NotificationType;
import com.dentify.domain.notification.enums.ReminderWindow;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity @Getter  @Setter @AllArgsConstructor
@NoArgsConstructor @Builder
@Table(
        name = "notifications",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_notification_dedup",
                        columnNames = {
                                "id_appointment",
                                "type",
                                "reminder_window",
                                "channel"
                        }
                )
        }
)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_notification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_appointment", nullable = false)
    private Appointment appointment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReminderWindow window;

    private String confirmation_token; //to validate that the patient is real
    private LocalDateTime dateGeneration;
    private LocalDateTime dateSent;

    private Integer maximumAttempts;
    private Integer attemptsMade;

    @Column(length = 500)
    private String errorMessage;

}

