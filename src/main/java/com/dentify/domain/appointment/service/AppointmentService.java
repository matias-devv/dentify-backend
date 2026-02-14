package com.dentify.domain.appointment.service;

import com.dentify.domain.agenda.model.Agenda;
import com.dentify.domain.agenda.service.IAgendaService;
import com.dentify.domain.appointment.dto.CreateAppointmentRequestDTO;
import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.appointment.repository.IAppointmentRepository;
import com.dentify.domain.appointment.dto.CreateAppointmentResponseDTO;
import com.dentify.domain.appointment.enums.AppointmentStatus;
import com.dentify.domain.patient.service.IPatientService;
import com.dentify.domain.pay.enums.PaymentMethod;
import com.dentify.domain.pay.enums.PaymentStatus;
import com.dentify.domain.patient.model.Patient;
import com.dentify.domain.pay.model.Pay;
import com.dentify.domain.pay.service.IPayService;
import com.dentify.domain.product.model.Product;
import com.dentify.domain.product.service.IProductService;
import com.dentify.domain.receipt.model.Receipt;
import com.dentify.domain.receipt.service.IReceiptService;
import com.dentify.integration.email.EmailService;
import com.dentify.integration.email.GenerateMailTokenService;
import com.dentify.integration.mercadopago.MercadoPagoService;
import com.dentify.domain.treatment.model.Treatment;
import com.dentify.domain.treatment.service.ITreatmentService;
import com.dentify.domain.user.model.AppUser;
import com.dentify.domain.user.service.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService {

    private final GenerateMailTokenService mailTokenService;
    private final EmailService emailService;
    private final IReceiptService receiptService;
    private final IAppointmentRepository appointmentRepository;
    private final ITreatmentService treatmentService;
    private final IPayService payService;
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

        //validations from agenda
        agendaService.validateCreateAppointment(agenda, dentist, product, request.date(), request.start_time());

        productService.validateIfProductIsActive(product);

        //validate time availability
        this.validateAppointmentAvailability( request.date(), request.start_time(), request.duration_minutes());

        //create or find active treatment for this patient and product
        Treatment treatment = treatmentService.findOrCreateTreatment( patient, product, dentist);

        //create appointment
        Appointment appointment = this.buildAppointment( patient, treatment, request, dentist, agenda);

        appointmentRepository.save( appointment);

        //create payment
        Pay pay = payService.savePayment(appointment, treatment, request, product);

        //if MercadoPago -> generate payment link
        String paymentLink = null;

        if (request.paymentMethod() == PaymentMethod.MERCADO_PAGO) {

            paymentLink = this.validateMercadoPagoPayment(pay);
        }

        //If pay in cash -> pay now or later
        if ( request.paymentMethod() == PaymentMethod.CASH){

             this.validateCashPayment(request, treatment, pay, appointment);
        }

        return this.buildResponse( patient, product, pay, treatment, request, appointment, paymentLink);
    }

    private String validateMercadoPagoPayment( Pay pay) {

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

        treatmentService.actualizeTreatmentOutstandingBalanceAndSetStatusInProgress(treatment, pay);

        payService.actualizePaymentStatusToPaidAndSetActualDate(pay);

        this.actualizeAppointmentStatusToConfirmed(appointment);

        //Generate receipt and send email
        try {

            Receipt receipt = receiptService.generateAndSaveReceipt(pay, appointment, null);

            emailService.sendPaymentReceipt(appointment, pay, receipt);

            log.info("Cash receipt generated and sent: {}", receipt.getFilename());
        } catch (Exception e) {
            log.error("Error generating/sending cash receipt", e);
        }
    }

    public void actualizeAppointmentStatusToConfirmed(Appointment appointment) {
        //change appointment status( SCHEDULED) -> CONFIRMED
        appointment.setAppointmentStatus(AppointmentStatus.CONFIRMED);

        appointmentRepository.save(appointment);
    }

    @Override
    public void cancelAppointment( AppointmentStatus typeOfCancellation, Appointment appointment, String message) {

        appointment.setAppointmentStatus( typeOfCancellation);

        appointment.setReason_for_cancellation(message);

        appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> findByDateBeforeAndAppointmentStatusIn(LocalDate date, List<AppointmentStatus> statuses) {
        return appointmentRepository.findByDateBeforeAndAppointmentStatusIn(date, statuses);
    }

    @Override
    public void markNoShow(Appointment appointment) {
        //change appointment status( SCHEDULED) -> NO SHOW
        appointment.setAppointmentStatus(AppointmentStatus.NO_SHOW);

        appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> findByAppointmentStatusAndDate(LocalDate targetDate, AppointmentStatus status) {
        return appointmentRepository.findByDateAndAppointmentStatus(targetDate, status );
    }

    @Override
    public List<Appointment> findByAppointmentStatusAndDateBetween(AppointmentStatus status, LocalDate startDate, LocalDate finalDate) {
        return appointmentRepository.findByAppointmentStatusAndDateBetween(status, startDate, finalDate);
    }

    @Override
    public List<Appointment> findScheduledAppointmentsBetween(LocalTime startTime, LocalTime finalTime) {
        return appointmentRepository.findByStartTimeBetween( startTime, finalTime);
    }

    @Override
    public List<Appointment> findReservedAppointmentsNotConfirmed(LocalDateTime startDate, LocalDateTime finalDate) {
        return appointmentRepository.findByAppointmentStatusAndAttendanceConfirmedAndStartTimeBetween(
                                                                                                      AppointmentStatus.SCHEDULED,
                                                                                     false,
                                                                                                      startDate,
                                                                                                      finalDate);
    }

    @Override
    public List<Appointment> findReservedAppointmentsConfirmed(LocalTime startTime, LocalTime finalTime) {
        LocalDate today = LocalDate.now();
        return appointmentRepository.findAppointmentsInRange(
                                                             AppointmentStatus.CONFIRMED,
                                                             true,
                                                             today,
                                                             today,
                                                             startTime,
                                                             finalTime);
    }

    @Override
    public List<Appointment> findByDateLessThanEqualAndAppointmentStatusIn(LocalDate today, List<AppointmentStatus> scheduled) {
        return appointmentRepository.findByDateLessThanEqualAndAppointmentStatusIn(today, scheduled);
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

    private Appointment buildAppointment(Patient patient, Treatment treatment, CreateAppointmentRequestDTO request,  AppUser dentist, Agenda agenda) {

        String notes = ( request.notes() != null) ? request.notes() : null;
        String instructions = ( request.patient_instructions() != null) ? request.patient_instructions() : null;

        return Appointment  .builder()
                            .notes( notes )
                            .patient_instructions( instructions)
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

                boolean isCancelled = this.validateAppointmentCancelation(appointment);

                if ( isCancelled ) {
                    continue; //ignore cancelled
                }

                LocalTime existingStart = appointment.getStartTime();
                LocalTime existingEnd = existingStart.plusMinutes(appointment.getDuration_minutes());

                //verify overlap
                if( startTime.isBefore(existingEnd) && endTime.isAfter(existingStart) ) {

                    throw new RuntimeException( String.format("Time slot not available. Conflict with appointment at %s", existingStart) );
                }
            }
        }
    }

    private boolean validateAppointmentCancelation(Appointment appointment) {
        if ( appointment.getAppointmentStatus() == AppointmentStatus.CANCELLED_BY_ADMIN){
            return true;
        }
        if ( appointment.getAppointmentStatus() == AppointmentStatus.CANCELLED_BY_PATIENT){
            return true;
        }
        if ( appointment.getAppointmentStatus() == AppointmentStatus.CANCELLED_BY_SYSTEM){
            return true;
        }
        return false;
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
