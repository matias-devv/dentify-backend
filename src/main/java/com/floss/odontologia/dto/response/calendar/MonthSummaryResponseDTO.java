package com.floss.odontologia.dto.response.calendar;

import java.time.LocalDate;
import java.util.List;

public record MonthSummaryResponseDTO(Long id_agenda,
                                      String agenda_name,
                                      LocalDate start_date_query,
                                      LocalDate final_date_query,
                                      LocalDate start_date_agenda,
                                      LocalDate final_date_agenda,
                                      List<DailySummaryDTO> dailySummary) {
}
