package com.floss.odontologia.dto.response.calendar;

import com.floss.odontologia.enums.AvailabilityState;

import java.time.DayOfWeek;
import java.time.LocalDate;

public record DailySummaryDTO(LocalDate date,
                              DayOfWeek day_of_week,
                              Integer available_slots,
                              Integer occupied_slots,
                              Integer total_slots,
                              AvailabilityState state) {
}
