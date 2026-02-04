package com.floss.odontologia.service.impl;

import com.floss.odontologia.dto.request.appointment.CreateAppointmentRequestDTO;
import com.floss.odontologia.dto.response.appointment.CreateAppointmentResponseDTO;
import com.floss.odontologia.enums.AppointmentStatus;
import com.floss.odontologia.enums.PaymentMethod;
import com.floss.odontologia.enums.PaymentStatus;
import com.floss.odontologia.enums.TreatmentStatus;
import com.floss.odontologia.model.*;
import com.floss.odontologia.repository.*;
import com.floss.odontologia.service.interfaces.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService {

    private final IAppointmentRepository appointmentRepository;
    private final ITreatmentRepository treatmentRepository;
    private final IPayRepository payRepository;
    private final IPatientService patientService;
    private final IProductService productService;
    private final IUserService userService;
    private final IAgendaService agendaService;
    private final MercadoPagoService mercadoPagoService;

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public CreateAppointmentResponseDTO saveAppointmentWithPay(CreateAppointmentRequestDTO request) {

        //find patient, product, dentist, agenda
        Patient patient = patientService.findPatientById(request.id_patient());

        Product product = productService.findProductById(request.id_product());

        AppUser dentist = userService.findUserById(request.id_dentist());

        Agenda agenda  = agendaService.findAgendaById( request.id_agenda());

        //validations
        agendaService.validateIfAgendaIsActive( agenda);

        agendaService.verifyIfThisAgendaBelongsToTheDentist( agenda, dentist);

        productService.validateIfProductIsActive( product);

        agendaService.validateAgendaAvailability( agenda, request.date(), request.start_time());

        //validate time availability
        this.validateAppointmentAvailability( request.date(), request.start_time(), request.duration_minutes());

        //create or find active treatment for this patient and product
        Treatment treatment = findOrCreateTreatment( patient, product, dentist);

        //create appointment
        Appointment appointment = this.buildAppointment( patient, treatment, request, dentist, agenda);

        appointmentRepository.save( appointment);

        //create payment
        Pay pay = this.buildPay( appointment, treatment, request, product);

        payRepository.save( pay);

        //if MercadoPago -> generate payment link
        String paymentLink = null;

        //If he/her pay with mercado pago
        if (request.paymentMethod() == PaymentMethod.MERCADO_PAGO) {

            paymentLink = this.validateMercadoPagoPayment(request, pay);
        }

        //If pay in cash -> pay now or later
        if ( request.paymentMethod() == PaymentMethod.CASH){

             this.validateCashPayment(request, treatment, pay, appointment);
        }

        return this.buildResponse( patient, product, pay, treatment, request, appointment, paymentLink);
    }

    private String validateMercadoPagoPayment(CreateAppointmentRequestDTO request, Pay pay) {

        String paymentLink = mercadoPagoService.createPaymentPreference(pay);

        return paymentLink;
    }

    private void validateCashPayment(CreateAppointmentRequestDTO request, Treatment treatment, Pay pay, Appointment appointment) {

        if ( request.payNow() == null){
            throw new RuntimeException("If he chose to pay in cash, it is necessary to know if he is paying now or on the day of the appointment");
        }
        if ( request.payNow()) {

            this.upgradeToPaidAppointment( treatment, pay, appointment);
        }
    }

    private void upgradeToPaidAppointment(Treatment treatment, Pay pay, Appointment appointment) {

        this.actualizeTreatmentOutstandingBalanceAndSetStatusInProgress(treatment, pay);

        this.actualizePaymentStatusToPaidAndSetActualDate(pay);

        this.actualizeAppointmentStatusToConfirmed(appointment);
    }

    private void actualizeTreatmentOutstandingBalanceAndSetStatusInProgress(Treatment treatment, Pay pay) {

        BigDecimal updatedAmount = treatment.getOutstanding_balance().subtract( pay.getAmount() );
        //set new outstanding balance
        treatment.setOutstanding_balance(updatedAmount);

        //actualize treatment status( CREATED) ->  IN PROGRESS
        if (treatment.getTreatmentStatus() == TreatmentStatus.CREATED) {
            treatment.setTreatmentStatus(TreatmentStatus.IN_PROGRESS);
        }
        treatmentRepository.save(treatment);
    }

    private void actualizePaymentStatusToPaidAndSetActualDate(Pay pay) {

        pay.setPayment_status(PaymentStatus.PAID);

        pay.setDate_generation(LocalDateTime.now());

        payRepository.save(pay);
    }

    private void actualizeAppointmentStatusToConfirmed(Appointment appointment) {
        //change appointment status( SCHEDULED) -> CONFIRMED
        appointment.setAppointmentStatus(AppointmentStatus.CONFIRMED);

        appointmentRepository.save(appointment);
    }

    private CreateAppointmentResponseDTO buildResponse(Patient patient, Product product, Pay pay, Treatment treatment,
                                                       CreateAppointmentRequestDTO request, Appointment appointment, String paymentLink) {
        return CreateAppointmentResponseDTO.builder()
                                            .id_appointment( appointment.getId_appointment())
                                            .id_treatment( treatment.getId_treatment())
                                            .id_pay( pay.getId_pay())
                                            .date( appointment.getDate())
                                            .start_time( appointment.getStartTime())
                                            .duration_minutes( request.duration_minutes())
                                            .end_time( appointment.getStartTime().plusMinutes( request.duration_minutes() ) )
                                            .amount_to_pay( pay.getAmount())
                                            .payment_method( pay.getPayment_method())
                                            .payment_link( paymentLink)
                                            .appointment_status( appointment.getAppointmentStatus())
                                            .payment_status( pay.getPayment_status())
                                            .product_name( product.getName_product())
                                            .build();
    }

    private Pay buildPay( Appointment appointment, Treatment treatment, CreateAppointmentRequestDTO request,  Product product) {
        return  Pay.builder()
                    .treatment(treatment)
                    .appointment(appointment)
                    .amount( product.getUnit_price()) // always from the product
                    .payment_method( request.paymentMethod())
                    .payment_status( PaymentStatus.PENDING)
                    .date_generation( LocalDateTime.now())
                    .build();
    }

    private Appointment buildAppointment(Patient patient, Treatment treatment, CreateAppointmentRequestDTO request,  AppUser dentist, Agenda agenda) {
        return Appointment  .builder()
                            .notes( request.notes())
                            .patient_instructions( request.patient_instructions())
                            .appointmentStatus(AppointmentStatus.SCHEDULED)
                            .date(request.date())
                            .startTime(request.start_time())
                            .duration_minutes( request.duration_minutes())
                            .app_user(dentist)
                            .patient(patient)
                            .treatment(treatment)
                            .agenda(agenda)
                            .build();
    }

    private void validateAppointmentAvailability(LocalDate date, LocalTime startTime, Integer duration) {

        LocalTime endTime = startTime.plusMinutes(duration);

        //find overlapping appointments
        List<Appointment> existingAppointments = appointmentRepository.findByDate(date);

        if (existingAppointments != null) {

            for (Appointment appointment : existingAppointments) {

                if ( appointment.getAppointmentStatus() == AppointmentStatus.CANCELLED) {
                    continue; //ignore cancelled
                }

                LocalTime existingStart = appointment.getStartTime();
                LocalTime existingEnd = existingStart.plusMinutes(appointment.getDuration_minutes());

                //verify overlap
                if( startTime.isBefore(existingEnd) && endTime.isAfter(existingStart)) {
                    throw new RuntimeException(
                            String.format("Time slot not available. Conflict with appointment at %s", existingStart)
                    );
                }
            }
        }
    }

    private Treatment findOrCreateTreatment(Patient patient, Product product, AppUser dentist) {

        // Find active treatment for this patient and service
        Optional<Treatment> existingTreatment = treatmentRepository.findByPatientAndProductAndTreatmentStatus( patient,
                                                                                                                product,
                                                                                                                TreatmentStatus.CREATED);

        if ( existingTreatment.isPresent() ) {
            return existingTreatment.get();
        }

        // if not exists -> new treatment
        Treatment newTreatment = this.buildTreatment( patient, product, dentist);

        treatmentRepository.save(newTreatment);

        return newTreatment;
    }

    private Treatment buildTreatment(Patient patient, Product product, AppUser dentist) {
        return Treatment.builder()
                        .base_price( product.getUnit_price())
                        .discount(BigDecimal.ZERO)
                        .final_price( product.getUnit_price())
                        .outstanding_balance( product.getUnit_price())
                        .treatmentStatus( TreatmentStatus.CREATED)
                        .start_date( LocalDateTime.now())
                        .app_user(dentist)
                        .patient(patient)
                        .product(product)
                        .build();
    }

    public void admitPatient(Long appointmentId) {

        Optional<Appointment> existingAppointment = appointmentRepository.findById(appointmentId);

        if ( existingAppointment.isPresent() ) {

            //validate if paymentStatus is PAID
            boolean isPaid = existingAppointment.get().getPays().stream()
                                                                     .anyMatch(p -> p.getPayment_status() == PaymentStatus.PAID);

            if ( !isPaid) {
                throw new RuntimeException("The patient cannot be admitted without confirmed payment");
            }
            existingAppointment.get().setAppointmentStatus(AppointmentStatus.ADMITTED);

            appointmentRepository.save( existingAppointment.get() );
        }
    }

//
//    @Override
//    public List<LocalTime> getHoursOfDentist(LocalDate choosenDate, Long id_dentist, String selectedDay) {
//        //Max has that amount to prevent it from returning too many time slots.
//        int max = 20;
//        int counter = 0;
//
//        //I get the schedules of the dentist
//        AppUser dentist = iDentistRepository.findById(id_dentist).orElse(null);
//        if ( dentist != null) {
//
//            List<Schedule> schedules = dentist.getSchedulesList();
//            List<LocalTime> hours = new ArrayList<>();
//
//            if (schedules != null) {
//                for (Schedule schedule : schedules) {
//
//                    boolean result = this.verifySchedule( schedule, choosenDate, selectedDay);
//
//                    //if result is ok
//                    if (result) {
//                        //this "slot" is a sort of "accumulator"
//                        LocalTime slot = schedule.getStartTime();
//
//                        //as long as the "accumulator" is less than the (end time - 30 minutes)
//                        while (slot.isBefore(schedule.getEndTime().minusMinutes(30))) {
//
//                            //I add 30 minutes to this initial “slot” and add it to the list of “hours.”
//                            slot = slot.plusMinutes(30);
//                            hours.add(slot);
//                            counter++;
//
//                            //If the slots are over "twenty" I return the list because is too much slots for one work day
//                            if (counter >= max) {
//                                return hours;
//                            }
//                        }
//                    }
//                }
//            }
//            //This removes the hours dedicated to other appointments -> they are not available for use
//            hours = this.checkAppointments(choosenDate, dentist, hours);
//            return hours;
//        }
//        return null;
//    }
//
//    private boolean verifySchedule(Schedule schedule, LocalDate choosenDate, String selectedDay) {
//        boolean result = true;
//        LocalDate today = LocalDate.now();
//
//            //if the schedule is not active -> null
//            if ( !schedule.isActive()) {
//                return false;
//            }
//            //if the day selected and the day in the schedule(String) are not equals -> null
//            if ( !schedule.getDayWeek().equalsIgnoreCase(selectedDay)) {
//                return false;
//            }
//            //if the startTime < endTime && the endTime > startTime -> null
//            if ( !schedule.getStartTime().isBefore(schedule.getEndTime()) && !schedule.getEndTime().isAfter(schedule.getStartTime())) {
//                return false;
//            }
//            //today must be before the end date(schedule).
//            if ( today.isAfter( schedule.getDate_to() )){
//                return false;
//            }
//            //the choosen date must be after the start date(schedule)
//            if ( !choosenDate.isAfter( schedule.getDate_from() )){
//                return false;
//            }
//            //the choosen date -> dayWeek(String) is not equals comparing with the dayWeek of the schedule(String) ?
//            if ( !choosenDate.getDayOfWeek().toString().equalsIgnoreCase( schedule.getDayWeek() ) ) {
//                return false;
//            }
//        return result;
//    }
//
//    @Override
//    public List<LocalTime> checkAppointments(LocalDate choosenDate, AppUser dentist, List<LocalTime> hours) {
//
//        List <Appointment> listAppo = dentist.getAppointmentList();
//        List <LocalTime> duplicateAppointments = new ArrayList<>();
//
//        //if the appointments are null -> the patient can use the full range of hours in the schedule
//        if( listAppo == null){
//            return hours;
//        }
//        for (Appointment appo : listAppo) {
//
//            //I catch the date of the appointment
//            LocalDate date = appo.getDate();
//
//            if( date.equals(choosenDate)){
//                //I go through the received hours of the schedule
//                for (LocalTime slot : hours){
//
//                    // If the start time of the current appointment equals the current slot ->
//                    // remove this slot of the list of hours
//                    if (appo.getStartTime().equals(slot) ){
//                        duplicateAppointments.add(slot);
//                    }
//                }
//            }
//        }
//        hours.removeAll(duplicateAppointments);
//        return hours;
//    }


}
