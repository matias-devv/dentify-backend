package com.floss.odontologia.model;

import com.floss.odontologia.enums.NotificationChannel;
import com.floss.odontologia.enums.NotificationStatus;
import com.floss.odontologia.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity @Getter  @Setter @AllArgsConstructor
@NoArgsConstructor @Table ( name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_notification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_appointment")
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pay")
    private Pay pay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column(nullable = false, length = 1000)
    private String message;

    private LocalDateTime dateGeneration;
    private LocalDateTime dateSent;

    private Integer maximumAttempts;
    private Integer attemptsMade;

    @Column(length = 500)
    private String errorMessage;
}

