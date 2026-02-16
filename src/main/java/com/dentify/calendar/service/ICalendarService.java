package com.dentify.calendar.service;

import com.dentify.calendar.dto.request.WeekRequest;
import com.dentify.calendar.dto.response.WeekResponse;
import org.jspecify.annotations.Nullable;

public interface ICalendarService {

    @Nullable WeekResponse getWeeklySlots( WeekRequest request);
}
