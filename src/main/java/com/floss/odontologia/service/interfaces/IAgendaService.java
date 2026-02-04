package com.floss.odontologia.service.interfaces;

import com.floss.odontologia.dto.request.calendar.AgendaRequestDTO;
import com.floss.odontologia.model.Agenda;
import com.floss.odontologia.model.AppUser;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface IAgendaService {

    public String save(AgendaRequestDTO agendaRequestDTO);

//    public List<AgendaResponseDTO> findAgendasByUser(Long idUserApp);

    public Agenda findAgendaById(Long idAgenda);

    void validateIfAgendaIsActive(Agenda agenda);

    void validateAgendaAvailability(Agenda agenda, @Future(message = "The date must be in the future.") LocalDate date, LocalTime start_time);

    void verifyIfThisAgendaBelongsToTheDentist(Agenda agenda, AppUser dentist);
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
