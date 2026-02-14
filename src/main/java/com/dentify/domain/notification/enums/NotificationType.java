package com.dentify.domain.notification.enums;

public enum NotificationType {

    // Appointments
    APPOINTMENT_SCHEDULED,
    APPOINTMENT_REMINDER,
    APPOINTMENT_RESCHEDULED,
    APPOINTMENT_CONFIRMATION,
    APPOINTMENT_CANCELLED,

    // Pays
    PAYMENT_RECEIVED,
    PAYMENT_RECEIPT_ISSUED,

    // Treatments
    TREATMENT_FOLLOW_UP,
    TREATMENT_EVALUATION,

    // Institutional
    SYSTEM_MESSAGE
}
