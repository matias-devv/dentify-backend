package com.floss.odontologia.enums;

public enum TreatmentStatus {

    CREATED,            // treatment created, with at least one appointment assigned
                        // clinical care has not yet begun

    IN_PROGRESS,        // at least one appointment has been ADMITTED or COMPLETED
                        // partial absences may occur without affecting the status

    COMPLETED,          // all scheduled appointments have been COMPLETED
                        // clinically completed (even if payments remain)

    ABANDONED,          // the patient stopped attending (repeated NO_SHOW)
                        // not formally canceled, useful for metrics

    PATIENT_CANCELLED,  // explicit cancellation by the patient

    FACILITY_CANCELLED, // cancelled by dentist/administration

    SYSTEM_CANCELLED    // automatically canceled (rules)
}
