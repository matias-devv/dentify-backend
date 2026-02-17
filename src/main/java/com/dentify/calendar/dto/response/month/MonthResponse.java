package com.dentify.calendar.dto.response.month;

import com.dentify.calendar.dto.response.day.DetailedDayResponse;

import java.util.List;

public record MonthResponse(Long id_agenda,
                            Integer year,
                            Integer month_number,
                            String month_name,
                            String product_name,
                            Integer duration_minutes,
                            List<DailySummaryResponse> days) {
}
