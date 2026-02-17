package com.dentify.calendar.dto.response.month;

import com.dentify.domain.agenda.enums.AvailabilityState;

import java.time.DayOfWeek;
import java.time.LocalDate;

public record DailySummaryResponse(LocalDate date,
                                   Integer number_day,
                                   DayOfWeek dayOfWeek,
                                   Integer freeSlots,
                                   Integer occupiedSlots,
                                   Integer totalSlots,
                                   AvailabilityState state) {
}
