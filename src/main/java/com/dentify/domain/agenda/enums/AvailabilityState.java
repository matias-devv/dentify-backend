package com.dentify.domain.agenda.enums;

public enum AvailabilityState {

    NO_SCHEDULE, // No schedules available that day (e.g., Sundays)
    FULL, // 0 slots available
    LOW_AVAILABILITY, // < 30% of slots available
    AVAILABLE // >= 30% of slots available
}
