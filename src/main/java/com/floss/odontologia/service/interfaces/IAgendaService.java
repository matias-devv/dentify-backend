package com.floss.odontologia.service.interfaces;

import com.floss.odontologia.dto.request.AgendaRequestDTO;
import com.floss.odontologia.dto.request.MonthDateRangeRequestDTO;
import com.floss.odontologia.dto.request.WeekDateRangeRequestDTO;
import com.floss.odontologia.dto.response.calendar.AgendaResponseDTO;
import com.floss.odontologia.dto.response.calendar.FullDailyResponseDTO;
import com.floss.odontologia.dto.response.calendar.MonthSummaryResponseDTO;
import com.floss.odontologia.dto.response.calendar.WeekSummaryResponseDTO;
import com.floss.odontologia.dto.request.DayRequestDTO;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface IAgendaService {

    public String save(AgendaRequestDTO agendaRequestDTO);

//    public List<AgendaResponseDTO> findAgendasByUser(Long idUserApp);
//
//    public Optional<AgendaResponseDTO> findAgendaById(Long idAgenda);
//
//    public String patchStatusAgenda(AgendaRequestDTO agendaRequestDTO);
//
//    public String editAgenda(AgendaRequestDTO agendaRequestDTO);
//
//    public @Nullable FullDailyResponseDTO getAllSlotsInDay(DayRequestDTO request);
//
//    public @Nullable WeekSummaryResponseDTO getAvailableSlotsInWeek(WeekDateRangeRequestDTO request);
//
//    public @Nullable MonthSummaryResponseDTO getSummaryOfTheMonth(MonthDateRangeRequestDTO request);

}
