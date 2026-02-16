package com.dentify.calendar.controller;

import com.dentify.calendar.dto.request.WeekRequest;
import com.dentify.calendar.dto.response.WeekResponse;
import com.dentify.calendar.service.ICalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final ICalendarService calendarService;

    /**
     * get slots by week
    **/
    @GetMapping("/weekly/slots")
    public ResponseEntity<WeekResponse> getWeeklySlots( @RequestBody WeekRequest request) {
        WeekResponse response = calendarService.getWeeklySlots(request);
        return ResponseEntity.ok().body(response);
    }

    /**
     * get slots by day
     **/
    @GetMapping("/day/slots")
    public ResponseEntity<WeekResponse> getDailySlots( @RequestBody WeekRequest request) {
        WeekResponse response = calendarService.getWeeklySlots(request);
        return ResponseEntity.ok().body(response);
    }
}
