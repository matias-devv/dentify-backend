package com.dentify.domain.notification.enums;

public enum PaymentNotificationConfig {

    RECEIPT_TO_PATIENT( NotificationType.PAYMENT_RECEIPT_ISSUED, NotificationChannel.EMAIL),
    CONFIRMATION_TO_PATIENT(NotificationType.APPOINTMENT_CONFIRMATION, NotificationChannel.EMAIL),

    RECEIPT_TO_DENTIST( NotificationType.PAYMENT_RECEIPT_ISSUED, NotificationChannel.EMAIL),
    CONFIRMATION_TO_DENTIST(NotificationType.APPOINTMENT_CONFIRMATION, NotificationChannel.EMAIL);

    private final NotificationType type;
    private final NotificationChannel channel;

    PaymentNotificationConfig(NotificationType type, NotificationChannel channel) {
        this.type = type;
        this.channel = channel;
    }

    public NotificationType getType() { return type; }
    public NotificationChannel getChannel() { return channel; }

}
