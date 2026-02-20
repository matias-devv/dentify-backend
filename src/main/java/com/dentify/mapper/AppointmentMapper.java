package com.dentify.calendar.mapper;

import com.dentify.calendar.dto.PayResponse;
import com.dentify.calendar.dto.response.*;
import com.dentify.domain.appointment.dto.response.AppointmentCancelledResponse;
import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.patient.dto.CancelledPatientResponse;
import com.dentify.domain.speciality.model.Speciality;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppointmentMapper {

    public FullAppointmentResponse toResponse(Appointment appointment) {

        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null");
        }

        return new FullAppointmentResponse(
                                            appointment.getId_appointment(),
                                            appointment.getAppointmentStatus(),
                                            appointment.getStartTime(),
                                            appointment.getStartTime().plusMinutes( appointment.getDuration_minutes() ),
                                            appointment.getDuration_minutes(),
                                             appointment.getAttendanceConfirmed(),
                                            appointment.getConfirmed_at(),

                                            buildPatientResponse(appointment),
                                            buildProductResponse(appointment),
                                            buildDentistResponse(appointment),
                                            buildAgendaResponse(appointment),
                                            buildTreatmentResponse(appointment),
                                            buildPayResponse(appointment),

                                            appointment.getNotes(),
                                            appointment.getPatient_instructions(),
                                            appointment.getReason_for_cancellation()
        );
    }

    private PatientResponse buildPatientResponse(Appointment appointment) {
        var patient = appointment.getPatient();

        if (patient == null) return null;

        return new PatientResponse(
                patient.getId_patient(),
                patient.getName(),
                patient.getSurname(),
                patient.getDni(),
                patient.getPhone_number(),
                patient.getDate_of_birth(),
                patient.getCoverageType()
        );
    }

    private ProductResponse buildProductResponse(Appointment appointment) {

        var treatment = appointment.getTreatment();
        if (treatment == null) return null;

        var product = treatment.getProduct();
        if (product == null) return null;

        return new ProductResponse(
                product.getId_product(),
                product.getName_product(),
                product.getUnit_price(),
                product.getDescription()
        );
    }

    private AppUserResponse buildDentistResponse(Appointment appointment) {

        var dentist = appointment.getApp_user();

        if (dentist == null) return null;

        return new AppUserResponse(
                dentist.getId_app_user(),
                dentist.getName(),
                dentist.getSurname(),
                dentist.getSpecialities().stream().map(Speciality::getName).collect(Collectors.toList()));
    }

    private AgendaResponse buildAgendaResponse(Appointment appointment) {
        var agenda = appointment.getAgenda();

        if (agenda == null) return null;

        return new AgendaResponse(
                agenda.getId_agenda(),
                agenda.getAgenda_name()
        );
    }

    private TreatmentResponse buildTreatmentResponse(Appointment appointment) {
        var treatment = appointment.getTreatment();

        if (treatment == null) return null;

        return new TreatmentResponse(
                treatment.getId_treatment(),
                treatment.getTreatmentStatus(),
                treatment.getOutstanding_balance()
        );
    }

    private List<PayResponse> buildPayResponse(Appointment appointment) {

        var pays = appointment.getPays();

        if (pays == null || pays.isEmpty()) {
            return List.of();
        }

        return pays.stream()
                .map(pay -> new PayResponse(
                        pay.getId_pay(),
                        pay.getAmount(),
                        pay.getPayment_method(),
                        pay.getPayment_status()
                ))
                .toList();
    }

    public AppointmentCancelledResponse toCancelledResponse(Appointment appointment) {

        var patient = appointment.getPatient();

        CancelledPatientResponse patientResponse = new CancelledPatientResponse(patient.getName(),
                                                                                patient.getSurname(),
                                                                                patient.getDni() );

        return new AppointmentCancelledResponse(appointment.getId_appointment(),
                                                appointment.getAppointmentStatus(),
                                                appointment.getDate(),
                                                appointment.getStartTime(),
                                                appointment.getStartTime().plusMinutes( appointment.getDuration_minutes() ),
                                                appointment.getReason_for_cancellation(),
                                                LocalDateTime.now(),
                                                patientResponse );
    }
}