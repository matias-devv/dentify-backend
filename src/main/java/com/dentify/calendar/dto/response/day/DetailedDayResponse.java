package com.dentify.calendar.dto.response.day;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public record DetailedDayResponse(Long id_agenda,
                                  Long id_product,
                                  LocalDate startDate,
                                  DayOfWeek dayOfWeek,
                                  int durationMinutes,
                                  //optional
                                  String productName,
                                  int totalSlots,
                                  int freeSlots,
                                  int occupiedSlots,
                                  List<DetailedSlotResponse> slots  ) {
}
