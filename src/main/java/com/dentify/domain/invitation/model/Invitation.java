package com.dentify.domain.invitation;


import com.dentify.domain.invitation.enums.InvitationStatus;
import com.dentify.domain.invitation.enums.InvitedRole;
import com.dentify.security.model.AuthUser;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, unique = true, length = 36)   // UUID
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "invited_role", nullable = false, length = 20)
    private InvitedRole invitedRole;               // DENTIST o SECRETARY

    // Quién envió la invitación (admin o dentista)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invited_by", nullable = false)
    private AuthUser invitedBy;

    // Solo presente cuando se invita a un secretario
    // Permite saber a qué dentista asignarlo al aceptar
    @Column(name = "dentist_id")
    private Long dentistId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private InvitationStatus status = InvitationStatus.PENDING;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;               // generalmente +48hs

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    public boolean isPending() {
        return this.status == InvitationStatus.PENDING;
    }
}
