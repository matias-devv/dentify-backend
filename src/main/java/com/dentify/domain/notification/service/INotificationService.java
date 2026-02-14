package com.dentify.domain.notification.service;

import com.dentify.domain.notification.enums.NotificationChannel;
import com.dentify.domain.notification.enums.NotificationType;
import com.dentify.domain.notification.enums.ReminderWindow;
import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.notification.model.Notification;

import java.util.List;
import java.util.Optional;

public interface INotificationService {

    Notification buildNotification(Appointment appointment, NotificationType type, NotificationChannel channel);

    void incrementAttempsMadeForThisNotification(Notification notification);

    public void markAsSent(Notification notification);

    public void markAsFailed(Notification notification, String error);

    Optional<Notification> createReminderIfNotExists(Appointment appointment, ReminderWindow reminderWindow);

    void markAsRetry(Notification notification);

    void registerAttempt(Notification notification, boolean success, String errorMessage);

    void saveAll(List<Notification> notifications);
}
