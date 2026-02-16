package com.dentify.calendar.service;

import com.dentify.calendar.dto.request.day.DetailedDayRequest;
import com.dentify.calendar.dto.request.week.WeekRequest;
import com.dentify.calendar.dto.response.day.DetailedDayResponse;
import com.dentify.calendar.dto.response.week.WeekResponse;
import org.jspecify.annotations.Nullable;

public interface ICalendarService {

    @Nullable WeekResponse getWeeklySlots( WeekRequest request);

    DetailedDayResponse getDailySlots(DetailedDayRequest request);
}
