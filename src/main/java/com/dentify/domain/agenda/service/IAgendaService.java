package com.dentify.domain.agenda.service;

import com.dentify.domain.agenda.dto.AgendaRequestDTO;
import com.dentify.domain.agenda.model.Agenda;
import com.dentify.domain.user.model.AppUser;
import com.dentify.domain.product.model.Product;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalTime;

public interface IAgendaService {

    public String save(AgendaRequestDTO agendaRequestDTO);

//    public List<AgendaResponseDTO> findAgendasByUser(Long idUserApp);

    public Agenda findAgendaById(Long idAgenda);

    void validateIfAgendaIsActive(Agenda agenda);

    void validateAgendaAvailability(Agenda agenda, @Future(message = "The date must be in the future.") LocalDate date, LocalTime start_time);

    void verifyIfThisAgendaBelongsToTheDentist(Agenda agenda, AppUser dentist);

    void validateCreateAppointment(Agenda agenda, AppUser dentist, Product product, @NotBlank(message = "The date is mandatory") @Future(message = "The date must be in the future.") LocalDate date, @NotBlank(message = "The start time is mandatory") LocalTime starTime);
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
