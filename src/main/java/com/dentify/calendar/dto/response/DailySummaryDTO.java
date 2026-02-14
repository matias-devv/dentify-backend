package com.dentify.calendar.dto.response;

import com.dentify.domain.agenda.enums.AvailabilityState;

import java.time.DayOfWeek;
import java.time.LocalDate;

public record DailySummaryDTO(LocalDate date,
                              DayOfWeek day_of_week,
                              Integer available_slots,
                              Integer occupied_slots,
                              Integer total_slots,
                              AvailabilityState state) {
}
