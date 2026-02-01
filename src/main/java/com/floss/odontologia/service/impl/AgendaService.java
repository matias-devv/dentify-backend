package com.floss.odontologia.service.impl;

import com.floss.odontologia.dto.request.AgendaRequestDTO;
import com.floss.odontologia.dto.request.DayRequestDTO;
import com.floss.odontologia.dto.request.MonthDateRangeRequestDTO;
import com.floss.odontologia.dto.request.WeekDateRangeRequestDTO;
import com.floss.odontologia.dto.response.calendar.*;
import com.floss.odontologia.dto.response.AppointmentResponseDTO;
import com.floss.odontologia.enums.AvailabilityState;
import com.floss.odontologia.model.*;
import com.floss.odontologia.repository.IAgendaRepository;
import com.floss.odontologia.service.interfaces.IAgendaService;
import com.floss.odontologia.service.interfaces.IProductService;
import com.floss.odontologia.service.interfaces.IUserService;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class AgendaService implements IAgendaService {

    @Autowired
    private IAgendaRepository agendaRepository;

    @Autowired
    private IUserService userService;

    @Autowired
    private IProductService productService;

    @Override
    public String save(AgendaRequestDTO request) {

        //get user
        AppUser user = userService.getUserEntityById(request.idUserApp());

        if ( user == null){
            throw new RuntimeException("The user was not found");
        }

        this.validateDates(request);

        this.validateName(request);

        this.validateNullSchedules(request);

        //create agenda
        Agenda agenda = this.setAttributesNewAgenda(request);

        if ( request.idProduct() != null){
            Product product = productService.getProductEntityById(request.idProduct());
            if ( product != null){
                agenda.setProduct(product);
            }
        }
        agenda.setApp_user(user);

        //add schedules
        request.schedules().forEach(schedule -> {

            this.validateSchedules(schedule);

            Schedule newSchedule = this.setAttributesNewSchedule(schedule);

            agenda.addSchedule(newSchedule);
        });

        Agenda savedAgenda = agendaRepository.save(agenda);

        return "The agenda was saved successfully";
    }

    private void validateSchedules(Schedule schedule) {

        if (schedule.getDuration_minutes() < 15) {
            throw new RuntimeException("Minimum block duration is 15 minutes");
        }
        if (schedule.getDuration_minutes() > 480) {
            throw new RuntimeException("Maximum block duration is 8 hours");
        }
        if (schedule.getDays() == null || schedule.getDays().isEmpty()) {
            throw new RuntimeException("Schedule must have at least one day selected");
        }

        long totalMinutes = Duration.between( schedule.getStart_time(), schedule.getEnd_time() ).toMinutes();

        if (totalMinutes < schedule.getDuration_minutes()) {
            throw new RuntimeException("Time range is shorter than block duration");
        }
    }

    private void validateNullSchedules(AgendaRequestDTO request) {
        if ( request.schedules() == null || request.schedules().isEmpty() ){
            throw new RuntimeException("The schedules are mandatory");
        }
    }

    private void validateName(AgendaRequestDTO request) {
        if( request.agendaName() == null || request.agendaName().isEmpty() ){
            throw new RuntimeException("the agenda name cannot be empty");
        }
        if( request.agendaName().length() < 3){
            throw new RuntimeException("the agenda name is too short");
        }
        if( request.agendaName().length() > 30 ){
            throw new RuntimeException("the agenda name is too long");
        }
    }

    private void validateDates(AgendaRequestDTO request) {
        if ( request.startDate().isBefore(LocalDate.now()) ){
            throw new RuntimeException("the date cannot be before the current date");
        }
        if ( request.finalDate().isBefore(request.startDate())){
            throw new RuntimeException("the start date cannot be before the final date");
        }
    }

    private Schedule setAttributesNewSchedule(Schedule schedule) {

        Schedule newSchedule = new Schedule();
        newSchedule.setDuration_minutes(schedule.getDuration_minutes());
        newSchedule.setStart_time(schedule.getStart_time());
        newSchedule.setEnd_time(schedule.getEnd_time());

        //add days
        newSchedule.setDays(new HashSet<>( schedule.getDays()));

        return newSchedule;
    }

    private Agenda setAttributesNewAgenda(AgendaRequestDTO agendaRequestDTO) {
        Agenda agenda = new Agenda();
        agenda.setAgenda_name(agendaRequestDTO.agendaName());
        agenda.setActive(agendaRequestDTO.active());
        agenda.setStart_date(agendaRequestDTO.startDate());
        agenda.setFinal_date(agendaRequestDTO.finalDate());
        return agenda;
    }
//
//    @Override
//    public List<AgendaResponseDTO> findAgendasByUser(Long idUserApp) {
//
//        List<Agenda> listAgenda = agendaRepository.findAll();
//        List<AgendaResponseDTO> listAgendaResponseDTO = new ArrayList<>();
//
//        if (listAgenda != null) {
//            for (Agenda agenda : listAgenda) {
//
//                if (agenda.getApp_user().getId_app_user() == idUserApp) {
//
//                    AgendaResponseDTO dto = this.convertEntityToDto(agenda);
//                    listAgendaResponseDTO.add(dto);
//                }
//            }
//        }
//        return listAgendaResponseDTO;
//    }
//
//    @Override
//    public Optional<AgendaResponseDTO> findAgendaById(Long idAgenda) {
//        Agenda agenda = agendaRepository.findById(idAgenda).orElse(null);
//        if (agenda == null) {
//            return null;
//        }
//        return Optional.of(this.convertEntityToDto(agenda));
//    }
//
//    @Override
//    public String patchStatusAgenda(AgendaRequestDTO agendaRequestDTO) {
//        Agenda agenda = agendaRepository.findById(agendaRequestDTO.id_agenda()).orElse(null);
//
//        if (agenda != null) {
//            agenda.setActive(agendaRequestDTO.active());
//            agendaRepository.save(agenda);
//            return "The status of the agenda was successfully saved";
//        }
//        return "The agenda with this id: " + agendaRequestDTO.id_agenda() + "was not found";
//    }
//
//    @Override
//    public String editAgenda(AgendaRequestDTO agendaRequestDTO) {
//
//        Agenda agenda = agendaRepository.findById(agendaRequestDTO.id_agenda()).orElse(null);
//        if (agenda != null) {
//
//            agenda.setAgenda_name(agendaRequestDTO.agendaName());
//            agenda.setStart_date(agendaRequestDTO.startDate());
//            agenda.setFinal_date(agendaRequestDTO.finalDate());
//            agenda.setActive(agendaRequestDTO.active());
//            agenda.setSchedules(agendaRequestDTO.schedules());
//
//            if (agendaRequestDTO.idProduct() != null) {
//                Product product = productService.validateIfProductExists(agendaRequestDTO.idUserApp());
//
//                if (product != null) {
//                    agenda.setProduct(product);
//                }
//            }
////            List<Schedule> removeList = new ArrayList<>();
////
////            //recorro lista de schedules del agendaRequestDTO
////            for (Schedule schedule : agendaRequestDTO.schedules()) {
////
////                for (Schedule oldSchedule : agenda.getSchedules()) {
////
////                    if (schedule.getId_schedule() != oldSchedule.getId_schedule()) {
//                        removeList.add(oldSchedule);
//                    }
////                if (schedule.getId_schedule() == oldSchedule.getId_schedule()) {
////                    oldSchedule = schedule;
////                }
////                if (schedule.getId_schedule() == null) {
////
////                    //llamar al service schedule, persistir
////                    //enlazar esa nueva entidad a el old schedule
////                    //persistir
////                }
//                }
//            }
//            if (!removeList.isEmpty()) {
//                agenda.getSchedules().removeAll(removeList);
//            }
//
//            agendaRepository.save(agenda);
//            return agenda.getAgenda_name() + "successfully updated";
//        }
//        return "the agenda does not exists";
//    }
//
//    @Override
//    public @Nullable FullDailyResponseDTO getAllSlotsInDay( DayRequestDTO request) {
//
//        Optional<Agenda> agenda = agendaRepository.findById(request.id_agenda());
//
//        if (agenda.isEmpty()) {
//            return null;
//        }
//
//        List<Schedule> agendaSchedules = agenda.get().getSchedules();
//        List<Appointment> appointments = agenda.get().getAppointments();
//
//        List<EventDetailResponseDTO> slots = new ArrayList<>();
//        Integer occupiedSlots = 0;
//        Integer freeSlots = 0;
//        Integer totalSlots = 0;
//
//        boolean okDateRangeAgenda = CheckDayDateInAgenda(agenda.get(),
//                                                         request.date());
//        if (okDateRangeAgenda == false) {
//            return null;
//        }
//
//        for (Schedule schedule : agendaSchedules) {
//
//            List<Day> listDays = schedule.getDays();
//
//            for ( Day day : listDays ) {
//
//                    if( day.getDayOfWeek().equals( request.date().getDayOfWeek() ) ) {
//
//                        if ( appointments != null && !appointments.isEmpty() ){
//
//                            for (Appointment appointment : appointments) {
//
//                                if (appointment.getDate().equals( request.date() )) {
//
//                                    slots.add( this.convertToBusyEventDetailDTO( request.date(), schedule, appointment));
//                                    occupiedSlots++;
//                                }
//                            }
//                        }
//                        freeSlots++;
//                        slots.add( this.convertToEventDetailFreeDTO( request.date(), schedule) );
//                    }
//                }
//        }
//
//        totalSlots = freeSlots + occupiedSlots;
//
//        FastRecap dayRecap = this.createFastRecap(totalSlots, freeSlots, occupiedSlots);
//
//        return this.convertToFullDailyResponseDTO( agenda.get(), request.date(), dayRecap, slots);
//    }

    private EventDetailResponseDTO convertToBusyEventDetailDTO(LocalDate date, Schedule schedule, Appointment appointment) {
        return new EventDetailResponseDTO("slot " + date.toString() + schedule.getStart_time().toString(),
                                          "BUSY",
                                           schedule.getStart_time(),
                                           schedule.getEnd_time(),
                                           schedule.getDuration_minutes(),
                                           this.createAppointmentResponseDTO(appointment));
    }

    private AppointmentResponseDTO createAppointmentResponseDTO(Appointment appointment) {
        return new AppointmentResponseDTO(appointment.getId_appointment(),
                                          appointment.getPatient().getId_patient(),
                                          appointment.getPatient().getName(),
                                          appointment.getPatient().getPhone_number(),
                                          appointment.getPatient().getEmail(),
                                          appointment.getAppointmentStatus(),
                                          appointment.getNotes());
    }

    private EventDetailResponseDTO convertToEventDetailFreeDTO(LocalDate date, Schedule schedule) {
        return new EventDetailResponseDTO("slot " + date.toString() + schedule.getStart_time().toString(),
                                         "FREE",
                                          schedule.getStart_time(),
                                          schedule.getEnd_time(),
                                          schedule.getDuration_minutes(),
                                          null);
    }

    private FastRecap createFastRecap(Integer totalSlots, Integer freeSlots, Integer occupiedSlots) {

        double result = ( freeSlots.doubleValue()  / totalSlots.doubleValue() ) * 100.00;

        return new FastRecap( totalSlots, freeSlots, occupiedSlots, result );
    }

    private @Nullable FullDailyResponseDTO convertToFullDailyResponseDTO(Agenda agenda, LocalDate date,
                                                                         FastRecap dayRecap, List<EventDetailResponseDTO> slots) {
        return new FullDailyResponseDTO( agenda.getId_agenda(),
                                         agenda.getAgenda_name(),
                                         date,
                                         dayRecap,
                                         slots);
    }

    private boolean CheckDayDateInAgenda(Agenda agenda, LocalDate requestDate) {
        return agenda.getStart_date().isBefore(requestDate) && agenda.getFinal_date().isAfter(requestDate);
    }
//
//    @Override
//    public @Nullable WeekSummaryResponseDTO getAvailableSlotsInWeek(WeekDateRangeRequestDTO weekDateRange) {
//
//        Optional<Agenda> agenda = agendaRepository.findById(weekDateRange.id_agenda());
//
//        if (agenda.isEmpty()) {
//            return null;
//        }
//        List<Schedule> agendaSchedules = agenda.get().getSchedules();
//        List<Appointment> appointments = agenda.get().getAppointments();
//        List<EventResponseDTO> eventList = new ArrayList<>();
//
//        boolean okDateRangeAgenda = CheckDateRangeAgenda(agenda.get(),
//                                                         weekDateRange.startDate(),
//                                                         weekDateRange.endDate());
//
//        if (okDateRangeAgenda == false) {
//            return null;
//        }
//
//        LocalDate startDate = weekDateRange.startDate();
//
//        List<LocalDate> dates = startDate.datesUntil( weekDateRange.endDate() ).toList();
//
//        //filtramos por dia semana
//        //necesito convertir datos del schedule en 1 evento
//        //necesito verificar que no este ocupado
//        //si esta ocupado lo tengo que traer al paciente, marcar el id del turno
//        for (Schedule schedule : agendaSchedules) {
//
//            List<Day> listDays = schedule.getDays();
//
//            for (LocalDate date : dates) {
//
//                for ( Day day : listDays ) {
//
//                    if( day.getDayOfWeek().equals( date.getDayOfWeek() ) ) {
//
//                        if ( appointments != null && !appointments.isEmpty() ){
//
//                            for (Appointment appointment : appointments) {
//
//                                if (appointment.getDate().equals(date)) {
//
//                                    eventList.add(this.convertToBusyEventDTO(date, schedule, appointment));
//                                }
//                            }
//                        }
//                        eventList.add( this.convertToEventFreeDTO(date, schedule) );
//                    }
//                }
//            }
//        }
//        return this.convertToWeekResponseDTO(agenda.get(), eventList);
//
//    }
//
//    @Override
//    public @Nullable MonthSummaryResponseDTO getSummaryOfTheMonth(MonthDateRangeRequestDTO request) {
//
//        Optional<Agenda> agenda = agendaRepository.findById( request.id_agenda());
//
//        if (agenda.isEmpty()) {
//            return null;
//        }
//        List<Schedule> agendaSchedules = agenda.get().getSchedules();
//        List<Appointment> appointments = agenda.get().getAppointments();
//        //aux list
//        List<DailySummaryDTO> summaryList = new ArrayList<>();
//
//
//        boolean okDateRangeAgenda = CheckDateRangeAgenda(agenda.get(),
//                request.start_date(),
//                request.final_date());
//
//        if (!okDateRangeAgenda) {
//            return null;
//        }
//
//        List<LocalDate> dates = request.start_date().datesUntil( request.final_date() ).toList();
//
//        for (Schedule schedule : agendaSchedules) {
//
//            Integer occupiedSlots = 0;
//            Integer freeSlots = 0;
//            Integer totalSlots = 0;
//
//            List<Day> listDays = schedule.getDays();
//
//            for (LocalDate date : dates) {
//
//                for ( Day day : listDays ) {
//
//                    if( day.getDayOfWeek().equals( date.getDayOfWeek() ) ) {
//
//                        if (appointments != null && !appointments.isEmpty()) {
//
//                            for (Appointment appointment : appointments) {
//
//                                if (appointment.getDate().equals(date)) {
//                                    occupiedSlots++;
//                                }
//                            }
//                        }
//                        freeSlots++;
//                    }
//                }
//
//                totalSlots = freeSlots + occupiedSlots;
//
//                AvailabilityState state = this.calculateAvailabilityState(freeSlots, totalSlots);
//
//                summaryList.add( this.createDailySummaryDTO(date, freeSlots, occupiedSlots, totalSlots, state ));
//
//            }
//        }
//        return this.convertToMonthSumaryResponseDTO(agenda.get(), request, summaryList);
//
//    }

    private AvailabilityState calculateAvailabilityState( Integer freeSlots, Integer totalSlots) {

        double result = ( freeSlots.doubleValue()  / totalSlots.doubleValue() ) * 100.00;

        if ( result >= 30.00){
            return AvailabilityState.AVAILABLE;         // >= 30% of slots available
        }
        if ( result < 30.00){
            return AvailabilityState.LOW_AVAILABILITY;  // < 30% of slots available
        }
        if ( freeSlots == 0 &&  totalSlots > 0){
            return AvailabilityState.FULL;              // 0 slots available
        }
        if ( totalSlots == 0){
            return AvailabilityState.NO_SCHEDULE;       // No schedules available that day (e.g., Sundays)
        }
        return null;
    }

    private DailySummaryDTO createDailySummaryDTO(LocalDate actualDate, int freeSlots, int occupiedSlots, int totalSlots, AvailabilityState state) {
        return new DailySummaryDTO(actualDate,
                                   actualDate.getDayOfWeek(),
                                   freeSlots,
                                   occupiedSlots,
                                   totalSlots,
                                   state);
    }

    private @Nullable MonthSummaryResponseDTO convertToMonthSumaryResponseDTO(Agenda agenda, MonthDateRangeRequestDTO request,
                                                                              List<DailySummaryDTO> summaryList) {
        return new MonthSummaryResponseDTO(agenda.getId_agenda(),
                                           agenda.getAgenda_name(),
                                           request.start_date(),
                                           request.final_date(),
                                           agenda.getStart_date(),
                                           agenda.getFinal_date(),
                                           summaryList );
    }

    private EventResponseDTO convertToBusyEventDTO(LocalDate date, Schedule schedule, Appointment appointment) {
        return new EventResponseDTO("slot" + date.toString() + schedule.getStart_time().toString(),
                                    date,
                                    schedule.getStart_time(),
                                    schedule.getEnd_time(),
                                    schedule.getDuration_minutes(),
                                    appointment.getId_appointment(),
                                    "BUSY",
                                    appointment.getPatient().getName(),
                                    appointment.getAppointmentStatus().toString(),
                                    appointment.getNotes());
    }

    private EventResponseDTO convertToEventFreeDTO(LocalDate date, Schedule schedule) {
        return new EventResponseDTO("slot" + date.toString() + schedule.getStart_time().toString(),
                                    date,
                                    schedule.getStart_time(),
                                    schedule.getEnd_time(),
                                    schedule.getDuration_minutes(),
                                    null,
                                    "FREE",
                                    null,
                                    null,
                                    null);
    }

    private WeekSummaryResponseDTO convertToWeekResponseDTO(Agenda agenda, List<EventResponseDTO> events) {
        return new WeekSummaryResponseDTO(agenda.getId_agenda(),
                                            agenda.getAgenda_name(),
                                            agenda.getStart_date(),
                                            agenda.getFinal_date(),
                                            events);
    }

    private boolean CheckDateRangeAgenda(Agenda agenda, @NotBlank LocalDate startDate, @NotBlank LocalDate endDate) {
        return agenda.getStart_date().isBefore(startDate) && agenda.getFinal_date().isAfter(endDate);
    }

    private AgendaResponseDTO convertEntityToDto(Agenda agenda) {
        return new AgendaResponseDTO(agenda.getId_agenda(),
                agenda.getAgenda_name(),
                agenda.getActive(),
                agenda.getStart_date(),
                agenda.getFinal_date(),
                agenda.getProduct().getId_product(),
                agenda.getProduct().getName_product(),
                agenda.getSchedules());
    }

    private Agenda convertDtoToEntity(Agenda agenda, AgendaRequestDTO agendaRequestDTO) {
        agenda.setAgenda_name(agendaRequestDTO.agendaName());
        agenda.setActive(agendaRequestDTO.active());
        agenda.setStart_date(agendaRequestDTO.startDate());
        agenda.setFinal_date(agendaRequestDTO.finalDate());
        agenda.setSchedules(agendaRequestDTO.schedules());
        return agenda;
    }
    }