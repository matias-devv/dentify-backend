package com.dentify.calendar.dto.response.week;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public record DayResponse(LocalDate date,
                          DayOfWeek dayOfWeek,
                          boolean isWorkingDay,
                          List<SlotResponse> slots) {
}
