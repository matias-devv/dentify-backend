package com.floss.odontologia.service.interfaces;

import com.floss.odontologia.dto.request.calendar.AgendaRequestDTO;

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
