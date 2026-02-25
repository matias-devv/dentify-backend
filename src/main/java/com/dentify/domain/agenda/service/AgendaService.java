package com.dentify.domain.agenda.service;

import com.dentify.domain.agenda.dto.AgendaRequestDTO;
import com.dentify.domain.agenda.model.Agenda;
import com.dentify.domain.agenda.repository.IAgendaRepository;
import com.dentify.domain.dentist.Dentist;
import com.dentify.domain.dentist.service.IDentistService;
import com.dentify.domain.product.model.Product;
import com.dentify.domain.product.service.IProductService;
import com.dentify.domain.schedule.model.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AgendaService implements IAgendaService {

    private final IAgendaRepository agendaRepository;
    private final IDentistService dentistService;
    private final IProductService productService;

    @Override
    public String save(AgendaRequestDTO request) {

        //get dentist
        Dentist dentist = dentistService.findDentistById( request.idDentist() );

        if ( dentist == null){
            throw new RuntimeException("The dentist was not found");
        }

        this.validateDates(request);

        this.validateName(request);

        this.validateNullSchedules(request);

        //create agenda
        Agenda agenda = this.setAttributesNewAgenda(request);

        if ( request.idProduct() != null){
            Product product = productService.findProductById(request.idProduct());
            if ( product != null){
                agenda.setProduct(product);
            }
        }
        agenda.setDentist(dentist);

        //add schedules
        request.schedules().forEach(schedule -> {

            this.validateSchedules(schedule, request.duration_minutes());

            Schedule newSchedule = this.setAttributesNewSchedule(schedule);

            agenda.addSchedule(newSchedule);
        });

        agendaRepository.save(agenda);

        return "The agenda was saved successfully";
    }

    @Override
    public Agenda findAgendaById(Long idAgenda) {
        return agendaRepository.findById( idAgenda ).orElseThrow( () -> new RuntimeException("Agenda not found"));
    }

    @Override
    public void validateIfAgendaIsActive(Agenda agenda) {

        if( !agenda.getActive()){
            throw new RuntimeException("Agenda is not active");
        }
    }

    @Override
    public void validateAgendaAvailability(Agenda agenda, LocalDate date, LocalTime start_time) {

        if (date.isBefore(agenda.getStart_date()) || date.isAfter(agenda.getFinal_date() ) )
            throw new RuntimeException("Date outside agenda range");
    }

    @Override
    public void verifyIfThisAgendaBelongsToTheDentist(Agenda agenda, Dentist dentist) {

        if ( !agenda.getDentist().getId().equals( dentist.getId() ) ){
            throw new RuntimeException("The dentist must own this appointment book");
        }
    }

    @Override
    public void validateCreateAppointment(Agenda agenda, Dentist dentist, Product product, LocalDate date, LocalTime starTime) {

        this.validateIfAgendaIsActive( agenda);

        this.verifyIfThisAgendaBelongsToTheDentist( agenda, dentist);

        this.validateAgendaAvailability( agenda, date, starTime);
    }

    private void validateSchedules(Schedule schedule, Integer duration_minutes) {

        if ( duration_minutes < 15) {
            throw new RuntimeException("Minimum block duration is 15 minutes");
        }
        if ( duration_minutes > 480) {
            throw new RuntimeException("Maximum block duration is 8 hours");
        }
        if (schedule.getDays() == null || schedule.getDays().isEmpty()) {
            throw new RuntimeException("Schedule must have at least one day selected");
        }

        long totalMinutes = Duration.between( schedule.getStart_time(), schedule.getEnd_time() ).toMinutes();

        if (totalMinutes < duration_minutes) {
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
        agenda.setDuration_minutes( agendaRequestDTO.duration_minutes());
        return agenda;
    }

    @Override
    public void validateDateRangeInAgenda(Agenda agenda, LocalDate startDate, LocalDate endDate) {

        if ( !agenda.getStart_date().isBefore(startDate) && !agenda.getFinal_date().isAfter(endDate) ) {
            throw new RuntimeException("The requested dates are not in the valid range defined in the calendar.");
        }
    }

    @Override
    public void validateDateWithinAgendaRange(Agenda agenda, LocalDate requestedDate) {

        boolean okDateRangeAgenda = this.isDayWithinAgendaRange( agenda, requestedDate );

        if (!okDateRangeAgenda) {
            throw new RuntimeException("The requested date it's not in the valid range defined in the agenda.");
        }
    }

    private boolean isDayWithinAgendaRange(Agenda agenda, LocalDate requestedDate) {
        return agenda.getStart_date().isBefore(requestedDate) && agenda.getFinal_date().isAfter(requestedDate);
    }

    @Override
    public Optional<Agenda> findAgendaWithSchedules(Long idAgenda) {
        return agendaRepository.findAgendaWithSchedules(idAgenda);
    }

    @Override
    public void validateIfTheAgendaExists(Optional<Agenda> agenda) {
        if ( agenda.isEmpty()){
            throw new RuntimeException("The agenda requested does not exist");
        }
    }

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

}