package com.dentify.calendar.service;

import com.dentify.calendar.dto.request.WeekRequest;
import com.dentify.calendar.dto.response.*;
import com.dentify.domain.agenda.model.Agenda;
import com.dentify.domain.agenda.service.IAgendaService;
import com.dentify.domain.appointment.dto.AppointmentResponseDTO;
import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.appointment.service.IAppointmentService;
import com.dentify.domain.schedule.model.Schedule;
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

        agendaService.isWeekWithinAgendaRange( agenda.get(), request);

        List<Appointment> appointments = appointmentService.findAppointmentsByAgendaAndDateRange( agenda.get().getId_agenda(), request.startDate(), request.endDate());

        Map<LocalDateTime, Appointment> mapAppointments = appointmentService.fillInAppointmentMap( appointments );

        LocalDate startDate = request.startDate();

        System.out.println("La lista de schedules esta vacia? "+ agenda.get().getSchedules().isEmpty() );

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
