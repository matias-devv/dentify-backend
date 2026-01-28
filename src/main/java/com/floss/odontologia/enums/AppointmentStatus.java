package com.floss.odontologia.enums;

public enum AppointmentStatus {

    SCHEDULED,      // appointment created, patient has not yet arrived
                    // can be canceled, can be admitted, payment is due

    ADMITTED,       // patient arrived and was registered at reception
                    // requires valid payment according to policy

    IN_ATTENTION,   // the dentist has started the appointment
                    // explicit status to allow for actual delays

    COMPLETED,      // medical care completed
                    // appointment closed, not editable

    NO_SHOW,        // patient did not show up (defined with a grace period)
                    // impacts patient metrics

    CANCELLED       // appointment canceled (by patient, receptionist, dentist, or system)
                    // the reason is handled separately
}
