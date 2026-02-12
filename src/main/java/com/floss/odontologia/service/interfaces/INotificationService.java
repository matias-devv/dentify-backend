package com.floss.odontologia.service.interfaces;

import com.floss.odontologia.enums.NotificationChannel;
import com.floss.odontologia.enums.NotificationType;
import com.floss.odontologia.enums.ReminderWindow;
import com.floss.odontologia.model.Appointment;
import com.floss.odontologia.model.Notification;

import java.util.Optional;

public interface INotificationService {

    Notification saveNotificationReminder(Appointment appointment, NotificationType type, NotificationChannel channel, ReminderWindow window);

    void incrementAttempsMadeForThisNotification(Notification notification);

    public void markAsSent(Notification notification);

    public void markAsFailed(Notification notification, String error);

    Optional<Notification> createReminderIfNotExists(Appointment appointment, ReminderWindow reminderWindow);

    void markAsRetry(Notification notification);

    void registerAttempt(Notification notification, boolean success, String errorMessage);
}
