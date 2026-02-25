package com.dentify.domain.appointment.service;

import com.dentify.calendar.dto.response.FullAppointmentResponse;
import com.dentify.domain.dentist.Dentist;
import com.dentify.domain.dentist.service.IDentistService;
import com.dentify.mapper.AppointmentMapper;
import com.dentify.domain.agenda.model.Agenda;
import com.dentify.domain.agenda.service.IAgendaService;
import com.dentify.domain.appointment.dto.request.CancelAppointmentRequest;
import com.dentify.domain.appointment.dto.request.CreateAppointmentRequestDTO;
import com.dentify.domain.appointment.dto.response.AppointmentCancelledResponse;
import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.appointment.repository.IAppointmentRepository;
import com.dentify.domain.appointment.dto.response.CreateAppointmentResponseDTO;
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
import com.dentify.integration.mercadopago.MercadoPagoService;
import com.dentify.domain.treatment.model.Treatment;
import com.dentify.domain.treatment.service.ITreatmentService;
import com.dentify.domain.userProfile.model.UserProfile;
import com.dentify.domain.userProfile.service.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.PageRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService {

    private final EmailService emailService;
    private final IReceiptService receiptService;
    private final IAppointmentRepository appointmentRepository;
    private final ITreatmentService treatmentService;
    private final IPayService payService;
    private final IPatientService patientService;
    private final IProductService productService;
    private final IDentistService dentistService;
    private final IAgendaService agendaService;
    private final MercadoPagoService mercadoPagoService;
    private final AppointmentMapper appointmentMapper;

    /**
     * querys
     */
    @Override
    public FullAppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findByIdWithAllDetails(id).orElseThrow(() -> new RuntimeException("Appointment not found"));
        return appointmentMapper.toResponse(appointment);
    }


    @Override
    public List<Appointment> findScheduledAppointmentsBetween(LocalTime startTime, LocalTime finalTime) {
        return appointmentRepository.findByStartTimeBetween(startTime, finalTime);
    }

    @Override
    public List<Appointment> findReservedAppointmentsNotConfirmed(LocalDateTime startDate, LocalDateTime finalDate) {
        return appointmentRepository.findByAppointmentStatusAndAttendanceConfirmedAndStartTimeBetween(
                AppointmentStatus.SCHEDULED, false, startDate, finalDate);
    }

    @Override
    public List<Appointment> findByDateLessThanEqualAndAppointmentStatusIn(LocalDate today, List<AppointmentStatus> statuses) {
        return appointmentRepository.findByDateLessThanEqualAndAppointmentStatusIn(today, statuses);
    }

    @Override
    public List<Appointment> findAppointmentsByAgendaAndDateRange(Long idAgenda, LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.findAppointmentsByAgendaAndDateRange(idAgenda, startDate, endDate);
    }

    @Override
    public List<Appointment> findAppointmentsByAgendaAndDate(Long agendaId, LocalDate date) {
        return appointmentRepository.findAppointmentsByAgendaAndDate(agendaId, date);
    }

    @Override
    public Optional<Appointment> findNextStartTime() {
        List<AppointmentStatus> validStatuses = List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.ADMITTED);
        List<Appointment> result = appointmentRepository.findNextAppointment(
                LocalDate.now(), LocalTime.now(), validStatuses, PageRequest.of(0, 1));
        return result.stream().findFirst();
    }

    @Override
    public Long countAppointmentsTodayExcludingStatuses(List<AppointmentStatus> statuses) {
        return appointmentRepository.countAppointmentsTodayExcludingStatuses(statuses);
    }

    @Override
    public Map<LocalDateTime, Appointment> fillInAppointmentMap(List<Appointment> listAppointments) {
        Map<LocalDateTime, Appointment> map = new HashMap<>();
        if (listAppointments != null) {
            listAppointments.forEach(appo -> {
                LocalDateTime fullTime = appo.getDate().atTime(appo.getStartTime());
                map.put(fullTime, appo);
            });
        }
        return map;
    }


    // ── Create ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CreateAppointmentResponseDTO saveAppointmentWithPay(CreateAppointmentRequestDTO request) {

        Patient patient = patientService.findPatientById(request.id_patient());
        Product product = productService.findProductById(request.id_product());
        Dentist dentist = dentistService.findDentistById(request.id_dentist());
        Agenda agenda = agendaService.findAgendaById(request.id_agenda());

        agendaService.validateCreateAppointment(agenda, dentist, product, request.date(), request.start_time());

        agendaService.verifyIfThisAgendaBelongsToTheDentist(agenda, dentist);

        productService.validateIfProductIsActive(product);

        validateAppointmentAvailability(request.date(), request.start_time(), request.duration_minutes());

        Treatment treatment = treatmentService.findOrCreateTreatment(patient, product, dentist);

        Appointment appointment = appointmentMapper.buildAppointment(patient, treatment, request, dentist, agenda);

        appointmentRepository.save(appointment);

        Pay pay = payService.savePayment(appointment, treatment, request, product);

        String paymentLink = null;

        if (request.paymentMethod() == PaymentMethod.MERCADO_PAGO) {
            paymentLink = mercadoPagoService.createPaymentPreference(pay);
        }

        if (request.paymentMethod() == PaymentMethod.CASH) {
            handleCashPayment(request, treatment, pay, appointment);
        }

        return appointmentMapper.buildCreateAppointmentResponse(patient, product, pay, treatment, request, appointment, paymentLink);
    }

    @Override
    public void actualizeAppointmentStatusToConfirmed(Appointment appointment) {

        appointment.setAppointmentStatus( AppointmentStatus.CONFIRMED);

        appointmentRepository.save(appointment);


    }

    // ── State transitions ─────────────────────────────────────────────────────

    /**
     * Admits a patient to their appointment.
     * The appointment can be admitted if:
     * (a) The treatment was fully paid upfront (outstanding_balance == 0), OR
     * (b) There is a PAID Pay linked specifically to this appointment.
     */
    @Override
    @Transactional
    public void admitPatient(Long appointmentId) {

        Appointment appointment = appointmentRepository.findByIdWithPays(appointmentId).orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!canBeAdmitted(appointment)) {
            throw new RuntimeException("The patient cannot be admitted without confirmed payment");
        }

        appointment.setAppointmentStatus(AppointmentStatus.ADMITTED);

        appointmentRepository.save(appointment);
    }

    @Override
    public void markNoShow(Appointment appointment) {

        appointment.setAppointmentStatus(AppointmentStatus.NO_SHOW);

        appointmentRepository.save(appointment);
    }

    /**
     * Internal cancel — used by system jobs (non-payment, chargebacks, etc.)
     */
    @Override
    public void cancelAppointment(AppointmentStatus typeOfCancellation, Appointment appointment, String message) {

        appointment.setAppointmentStatus(typeOfCancellation);

        appointment.setReason_for_cancellation(message);

        appointmentRepository.save(appointment);
    }

    /**
     * Manual cancel — called from controller by secretary or dentist.
     * Uses the single CANCELLED status; the reason clarifies who cancelled.
     */
    @Override
    @Transactional
    public AppointmentCancelledResponse cancelAppointment(CancelAppointmentRequest request) {

        Appointment appointment = appointmentRepository.findByIdWithPatient(request.id_appointment())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if ( this.isInTerminalState(appointment) ) {
            throw new RuntimeException("The appointment cannot be cancelled in its current state: "
                    + appointment.getAppointmentStatus());
        }

        appointment.setReason_for_cancellation(request.reason_for_cancellation());

        appointment.setCancelled_at(LocalDateTime.now());

        appointment.setAppointmentStatus( AppointmentStatus.fromString( request.cancelledBy() ) );

        // The reason field already stores who cancelled and why.
        // I believe the best course of action is to email both the patient and the dentist.
        emailService.sendAppointmentManuallyCancelled(appointment);

        appointmentRepository.save(appointment);

        return appointmentMapper.toCancelledResponse(appointment);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private boolean isInTerminalState(Appointment appointment) {

        AppointmentStatus status = appointment.getAppointmentStatus();

        // Can only cancel appointments that haven't been attended yet
        return status == AppointmentStatus.COMPLETED
                || status == AppointmentStatus.CANCELLED_BY_DENTIST
                || status == AppointmentStatus.CANCELLED_BY_SECRETARY
                || status == AppointmentStatus.CANCELLED_BY_SYSTEM
                || status == AppointmentStatus.CANCELLED_BY_PATIENT
                || status == AppointmentStatus.NO_SHOW;
    }

    /**
     * A treatment paying upfront covers all its appointments.
     * If not, this specific appointment must have its own PAID payment.
     */
    private boolean canBeAdmitted(Appointment appointment) {

        // Case 1: full treatment paid upfront
        if ( appointment.getTreatment() != null && treatmentService.isCoveredByAdvancePayment(appointment.getTreatment() ) ) {
            return true;
        }

        // Case 2: this specific appointment has a confirmed payment
        return appointment.getPays().stream().anyMatch(p -> p.getPayment_status() == PaymentStatus.PAID);
    }


    private void handleCashPayment(CreateAppointmentRequestDTO request, Treatment treatment, Pay pay, Appointment appointment) {

        if (request.payNow() == null) {
            throw new RuntimeException("For cash payments, 'payNow' must be specified");
        }
        if (request.payNow()) {
            upgradeToPaidAppointment(treatment, pay, appointment);
        }
    }

    private void upgradeToPaidAppointment(Treatment treatment, Pay pay, Appointment appointment) {

        treatmentService.actualizeTreatmentOutstandingBalanceAndSetStatusInProgress(treatment, pay);

        payService.actualizePaymentStatusToPaidAndSetActualDate(pay);

        try {

            Receipt receipt = receiptService.generateAndSaveReceipt(pay, appointment, null);

            emailService.sendPaymentReceipt(appointment, pay, receipt);

            log.info("Cash receipt generated and sent: {}", receipt.getFilename());
        } catch (Exception e) {
            log.error("Error generating/sending cash receipt", e);
        }
    }

    private void validateAppointmentAvailability(LocalDate date, LocalTime startTime, Integer duration) {

        LocalTime endTime = startTime.plusMinutes(duration);
        List<Appointment> existing = appointmentRepository.findByDate(date);

        if (existing == null) return;

        for (Appointment appo : existing) {

            if ( appo.isCancelled() ) continue;

            LocalTime existingStart = appo.getStartTime();

            LocalTime existingEnd = existingStart.plusMinutes(appo.getDuration_minutes());

            if (startTime.isBefore(existingEnd) && endTime.isAfter(existingStart)) {

                throw new RuntimeException(
                        String.format("Time slot not available. Conflict with appointment at %s", existingStart));
            }
        }
    }
}