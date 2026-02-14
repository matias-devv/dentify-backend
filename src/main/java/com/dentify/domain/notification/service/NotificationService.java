package com.dentify.domain.notification.service;

import com.dentify.domain.notification.enums.NotificationChannel;
import com.dentify.domain.notification.enums.NotificationStatus;
import com.dentify.domain.notification.enums.NotificationType;
import com.dentify.domain.notification.enums.ReminderWindow;
import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.notification.model.Notification;
import com.dentify.domain.notification.repository.INotificationRepository;
import com.dentify.integration.email.GenerateMailTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {

    private final INotificationRepository notificationRepository;
    private final GenerateMailTokenService mailTokenService;

    @Override
    public Notification buildNotification(Appointment appointment, NotificationType type, NotificationChannel channel) {
        return Notification .builder()
                            .appointment(appointment)
                            .type(type)
                            .channel(channel)
                            .status( NotificationStatus.PENDING)
                            .confirmation_token( mailTokenService.generateConfirmationToken() )
                            .dateGeneration(LocalDateTime.now())
                            .maximumAttempts(3)
                            .attemptsMade(0)
                            .build();
    }

    @Override
    public void incrementAttempsMadeForThisNotification(Notification notification) {
        notification.setAttemptsMade( notification.getAttemptsMade() + 1);

        notificationRepository.save(notification);
    }

    @Transactional
    public void markAsSent(Notification notification) {

        notification.setStatus(NotificationStatus.SENT);

        notification.setDateSent(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    @Transactional
    public void markAsFailed(Notification notification, String error) {

        notification.setStatus(NotificationStatus.FAILED_FINAL);

        notification.setErrorMessage(error);

        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public Optional<Notification> createReminderIfNotExists(Appointment appointment, ReminderWindow window) {

        try {
            Notification notification = this.buildNotification( appointment, NotificationType.APPOINTMENT_REMINDER, NotificationChannel.EMAIL);

            notification.setWindow(window);

            notificationRepository.save(notification);

            return Optional.of( notification);

        }catch (DataIntegrityViolationException e) {
            //It exists -> empty
            return Optional.empty();
        }
    }

    @Override
    public void markAsRetry(Notification notification) {

        notification.setStatus(NotificationStatus.RETRY);

        notification.setAttemptsMade( notification.getAttemptsMade() + 1);

        notificationRepository.save(notification);
    }

    @Override
    public void registerAttempt(Notification notification, boolean success, String errorMessage) {

        notification.setAttemptsMade( notification.getAttemptsMade() + 1);

        if ( success ) {

            notification.setStatus( NotificationStatus.SENT );

            notification.setErrorMessage(null);
        }
        else if ( notification.getAttemptsMade() < notification.getMaximumAttempts()) {

            notification.setStatus( NotificationStatus.RETRY );

            notification.setErrorMessage(errorMessage);
        }
        else {

            notification.setStatus( NotificationStatus.FAILED_FINAL );

            notification.setErrorMessage( errorMessage);
        }
        notificationRepository.save( notification);
    }

    @Override
    public void saveAll(List<Notification> notifications) {
        notificationRepository.saveAll(notifications);
    }

}
