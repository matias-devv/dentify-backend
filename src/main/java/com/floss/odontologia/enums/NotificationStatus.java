package com.floss.odontologia.enums;

public enum NotificationStatus {

    PENDING,        // created, not yet sent
    SENT,           // sent successfully
    FAILED,         // final ruling
    INVALID_TARGET  // invalid email or phone number
}
