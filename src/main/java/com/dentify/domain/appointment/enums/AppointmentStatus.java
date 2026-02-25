package com.dentify.domain.appointment.enums;


public enum AppointmentStatus {

    SCHEDULED,      // appointment created, patient has not yet arrived
                    // can be canceled, can be admitted

    CONFIRMED,      // the payment has already been made or patient confirmed attendance (48 hours prior)


    ADMITTED,       // patient arrived and was registered at reception
                    // requires valid payment according to policy

    IN_ATTENTION,   // the dentist has started the appointment
                    // explicit status to allow for actual delays

    PENDING_CANCELLATION,  // Patient didn't confirm in 24 hours, last chance

    CANCELLED_BY_SYSTEM,   // The system cancelled it due to lack of confirmation

    CANCELLED_BY_PATIENT,   // Patient reported absence

    CANCELLED_BY_DENTIST,

    CANCELLED_BY_SECRETARY,

    COMPLETED,      // medical care completed
                    // appointment closed, not editable

    NO_SHOW;        // patient did not show up (defined with a grace period)
                    // impacts patient metrics

    public static AppointmentStatus fromString(String type) {
        try {
            return AppointmentStatus.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid appointment status: " + type);
        }
    }
}
