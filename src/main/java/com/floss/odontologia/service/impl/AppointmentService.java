package com.floss.odontologia.service.impl;

import com.floss.odontologia.dto.request.Appointment.createAppointmentRequestDTO;
import com.floss.odontologia.dto.response.Appointment.createAppointmentResponseDTO;
import com.floss.odontologia.dto.response.AppointmentDTO;
import com.floss.odontologia.enums.AppointmentStatus;
import com.floss.odontologia.enums.PaymentMethod;
import com.floss.odontologia.enums.PaymentStatus;
import com.floss.odontologia.enums.TreatmentStatus;
import com.floss.odontologia.model.*;
import com.floss.odontologia.repository.*;
import com.floss.odontologia.service.interfaces.IAppointmentService;
import com.floss.odontologia.service.interfaces.IPatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService {

    private final IAppointmentRepository appointmentRepository;
    private final ITreatmentRepository treatmentRepository;
    private final IPatientRepository patientRepository;
    private final IProductRepository productRepository;
    private final IPayRepository payRepository;
    private final MercadoPagoService mercadoPagoService;

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    @Override
    public createAppointmentResponseDTO createAppointmentWithPay(createAppointmentRequestDTO request) {

        //validate patient and product
        Patient patient = patientRepository.findById( request.id_patient() )
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Product product = productRepository.findById( request.id_product() )
                .orElseThrow(() -> new RuntimeException("Product not found"));

        //create or find the active treatment
        Treatment treatment = Treatment.builder()
                .patient(patient)
                .product(product)
                .base_price(product.getUnit_price())
                .discount(BigDecimal.ZERO)
                .final_price(product.getUnit_price())
                .outstanding_balance( product.getUnit_price()) //call method to calculate this
                .treatment_status(TreatmentStatus.CREATED)
                .start_date(LocalDateTime.now())
                .build();

        treatmentRepository.save(treatment);

        //create appointment
        Appointment appointment = Appointment.builder()
                .patient( patient)
                .treatment( treatment)
                .date( request.date() )
                .startTime( request.start_time() )
                .duration_minutes( request.duration_minutes() )
                .appointmentStatus( AppointmentStatus.SCHEDULED )
                .build();

        appointmentRepository.save(appointment);

        //create pay
        Pay pay = Pay.builder()
                .treatment( treatment)
                .appointment( appointment)
                .amount( product.getUnit_price() )
                .payment_method( request.paymentMethod() )
                .payment_status( PaymentStatus.PENDING)
                .date_generation( LocalDateTime.now())
                .build();

        payRepository.save(pay);

        //If MercadoPago is present -> generate link

        String payLink = null;
        if (request.paymentMethod() == PaymentMethod.MERCADO_PAGO) {
            payLink = mercadoPagoService.createPaymentPreference(pay);
            log.info("payment link generated: {}", payLink);
        }

        return createAppointmentResponseDTO.builder()
                .id_appointment( appointment.getId_appointment())
                .id_treatment( treatment.getId_treatment())
                .id_pay( pay.getId_pay())
                .pay_link( payLink)
                .appointment_status( appointment.getAppointmentStatus())
                .build();
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
