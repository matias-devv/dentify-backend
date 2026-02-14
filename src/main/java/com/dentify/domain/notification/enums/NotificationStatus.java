package com.dentify.domain.notification.enums;

public enum NotificationStatus {

    PENDING,        // created, not yet sent
    SENT,           // sent successfully
    RETRY,
    FAILED_FINAL,         // final ruling
    INVALID_TARGET  // invalid email or phone number
}
