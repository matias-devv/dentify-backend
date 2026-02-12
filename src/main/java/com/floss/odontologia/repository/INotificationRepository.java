package com.floss.odontologia.repository;

import com.floss.odontologia.enums.NotificationChannel;
import com.floss.odontologia.enums.NotificationType;
import com.floss.odontologia.enums.ReminderWindow;
import com.floss.odontologia.model.Appointment;
import com.floss.odontologia.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface INotificationRepository extends JpaRepository<Notification, Long> {

}