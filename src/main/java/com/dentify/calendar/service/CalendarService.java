package com.dentify.calendar.service;

import com.dentify.calendar.dto.request.day.DetailedDayRequest;
import com.dentify.calendar.dto.request.week.WeekRequest;
import com.dentify.calendar.dto.response.*;
import com.dentify.calendar.dto.response.day.DetailedDayResponse;
import com.dentify.calendar.dto.response.day.DetailedSlotResponse;
import com.dentify.calendar.dto.response.week.DayResponse;
import com.dentify.calendar.dto.response.week.SlotResponse;
import com.dentify.calendar.dto.response.week.WeekResponse;
import com.dentify.domain.agenda.model.Agenda;
import com.dentify.domain.agenda.service.IAgendaService;
import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.appointment.service.IAppointmentService;
import com.dentify.domain.schedule.model.Schedule;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CalendarService implements ICalendarService{

    private final IAgendaService agendaService;
    private final IAppointmentService appointmentService;

    @Override
    public @Nullable WeekResponse getWeeklySlots(WeekRequest request) {

        List<DayResponse> dayResponses = new ArrayList<>();
        Optional<Agenda> agenda = agendaService.findAgendaWithSchedules( request.id_agenda() );

        if ( agenda.isEmpty() ) {
            throw new RuntimeException("The agenda was not found");
        }

        agendaService.validateWeekWithinAgendaRange( agenda.get(), request);

        List<Appointment> appointments = appointmentService.findAppointmentsByAgendaAndDateRange( agenda.get().getId_agenda(), request.startDate(), request.endDate());

        Map<LocalDateTime, Appointment> mapAppointments = appointmentService.fillInAppointmentMap( appointments );

        LocalDate startDate = request.startDate();

        List<LocalDate> dates = startDate.datesUntil( request.endDate() ).toList();

        //filtramos por dia semana
        //necesito convertir datos del schedule en 1 evento
        //necesito verificar que no este ocupado
        //si esta ocupado lo tengo que traer al paciente, marcar el id del turno
        for( LocalDate date : dates ) {

            DayOfWeek dayOfWeek = date.getDayOfWeek();

            for( Schedule schedule : agenda.get().getSchedules() ){

                if( schedule.getDays().contains(dayOfWeek) ){

                    DayResponse dayResponse = this.buildDayResponse(date, schedule, mapAppointments);

                    dayResponses.add(dayResponse);
                }
            }
        }
        WeekResponse response = this.buildWeekResponse( agenda.get(), dayResponses);

        return response;
    }

    private WeekResponse buildWeekResponse(Agenda agenda, List<DayResponse> dayResponses) {
        return new WeekResponse(agenda.getId_agenda(),
                                agenda.getAgenda_name(),
                                agenda.getStart_date(),
                                agenda.getFinal_date(),
                                dayResponses);
    }

    private DayResponse buildDayResponse(LocalDate date, Schedule schedule, Map<LocalDateTime, Appointment> mapAppointments) {

        List<SlotResponse> slots = this.calculateSlotsForThisDay( date, schedule, mapAppointments);

        return new DayResponse(date,
                               date.getDayOfWeek(),
                               slots );
    }

    private List<SlotResponse> calculateSlotsForThisDay( LocalDate date, Schedule schedule, Map<LocalDateTime, Appointment> mapAppointments) {

        List<SlotResponse> slots = new ArrayList<>();

        LocalTime currentTime = schedule.getStart_time();

        LocalTime endTime = schedule.getEnd_time();

        int duration = schedule.getAgenda().getDuration_minutes();

        // As long as the slot fits before the final hour
        while( !currentTime.plusMinutes(duration).isAfter(endTime) ) {

            LocalTime slotEnd = currentTime.plusMinutes(duration);

            LocalDateTime key = date.atTime(currentTime);

            Appointment appointment = mapAppointments.get(key);

            SlotResponse slot = (appointment != null)
                    ? new SlotResponse(currentTime, slotEnd, false, buildAppointmentResponse(appointment) )
                    : new SlotResponse(currentTime, slotEnd, true, null );

            currentTime = slotEnd; //I advance to the next slot

            slots.add( slot);
        }
        return slots;
    }

    private AppointmentResponse buildAppointmentResponse(Appointment appointment) {
        return new AppointmentResponse( appointment.getId_appointment(),
                                        appointment.getPatient().getName(),
                                        appointment.getPatient().getSurname(),
                                        appointment.getAppointmentStatus(),
                                        appointment.getTreatment().getProduct().getName_product() );
    }

    @Override
    public DetailedDayResponse getDailySlots(DetailedDayRequest request) {

        List<DetailedSlotResponse> slots = new ArrayList<>();

        Optional<Agenda> agenda = agendaService.findAgendaWithSchedules( request.id_agenda() );

        if ( agenda.isEmpty() ) {
            throw new RuntimeException("The agenda was not found");
        }

        agendaService.validateDateWithinAgendaRange( agenda.get(), request.startDate());

        List<Appointment> appointments = appointmentService.findAppointmentsByAgendaAndDate( agenda.get().getId_agenda(), request.startDate() );

        Map<LocalDateTime, Appointment> mapAppointments = appointmentService.fillInAppointmentMap( appointments );

        DayOfWeek dayOfWeek =  request.startDate().getDayOfWeek();

        for ( Schedule schedule :  agenda.get().getSchedules() ) {

            if ( schedule.getDays().contains( dayOfWeek ) ){

                slots.addAll( this.buildListOfDetailedSlots( request.startDate(), schedule, mapAppointments) );

            }
        }

        Integer freeSlots = this.countAvailableSlots(slots);
        Integer ocuppiedSlots = this.countOcuppiedSlots(slots);
        Integer totalSlots = freeSlots + ocuppiedSlots;

        return this.buildDetailedDayResponse( agenda.get(), slots, request.startDate(), totalSlots, freeSlots, ocuppiedSlots);
    }

    private List<DetailedSlotResponse> buildListOfDetailedSlots(LocalDate date, Schedule schedule,
                                                                Map<LocalDateTime, Appointment> mapAppointments) {


        List<DetailedSlotResponse> slots = new ArrayList<>();

        LocalTime currentTime = schedule.getStart_time();

        LocalTime endTime = schedule.getEnd_time();

        int duration = schedule.getAgenda().getDuration_minutes();

        // As long as the slot fits before the final hour
        while( !currentTime.plusMinutes(duration).isAfter(endTime) ) {

            LocalTime slotEnd = currentTime.plusMinutes(duration);

            LocalDateTime key = date.atTime(currentTime);

            Appointment appointment = mapAppointments.get(key);

            DetailedSlotResponse slot = (appointment != null)
                    ? new DetailedSlotResponse(currentTime, slotEnd, false, buildDetailedAppointmentResponse(appointment) )
                    : new DetailedSlotResponse(currentTime, slotEnd, true, null );

            currentTime = slotEnd; //I advance to the next slot

            slots.add( slot);
        }
        return slots;
    }

    private Integer countAvailableSlots(List<DetailedSlotResponse> slots) {
        int freeSlots = 0;

        for ( DetailedSlotResponse slot : slots) {
            if ( slot.availability() == true ){
                freeSlots++;
            }
        }
        return freeSlots;
    }

    private Integer countOcuppiedSlots(List<DetailedSlotResponse> slots) {
        int occupiedSlots = 0;

        for ( DetailedSlotResponse slot : slots) {
            if ( slot.availability() == false ){
                occupiedSlots++;
            }
        }
        return occupiedSlots;
    }

    private DetailedAppointmentResponse buildDetailedAppointmentResponse(Appointment appointment) {
        return new DetailedAppointmentResponse(appointment.getId_appointment(),
                                               appointment.getPatient().getName(),
                                               appointment.getPatient().getSurname(),
                                               appointment.getPatient().getPhone_number(),
                                               appointment.getAppointmentStatus(),
                                               appointment.getTreatment().getProduct().getName_product(),
                                               appointment.getNotes() );
    }

    private DetailedDayResponse buildDetailedDayResponse(Agenda agenda, List<DetailedSlotResponse> slots, LocalDate requestedDate,
                                                         Integer totalSlots, Integer freeSlots, Integer occupiedSlots) {
        return new DetailedDayResponse(agenda.getId_agenda(),
                                       agenda.getProduct().getId_product(),
                                       requestedDate,
                                       requestedDate.getDayOfWeek(),
                                       agenda.getDuration_minutes(),
                                       agenda.getProduct().getName_product(),
                                       totalSlots,
                                       freeSlots,
                                       occupiedSlots,
                                       slots );
    }


//    private WeekResponse convertToWeekResponseDTO(Agenda agenda, List<EventResponse> events) {
//       WeekResponse response = new WeekResponse(agenda.getId_agenda(),
//                                    agenda.getAgenda_name(),
//                                    agenda.getStart_date(),
//                                    agenda.getFinal_date()
////                                    events
//                                    );
//
//        System.out.println(response.toString());
//        return response;
//    }
}
