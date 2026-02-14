package com.dentify.scheduler;
import com.dentify.domain.appointment.enums.AppointmentStatus;
import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.appointment.service.IAppointmentService;
import com.dentify.domain.notification.enums.ReminderWindow;
import com.dentify.domain.notification.service.INotificationService;
import com.dentify.domain.patientstat.model.PatientStat;
import com.dentify.domain.patientstat.service.IPatientStatService;
import com.dentify.domain.pay.enums.PaymentMethod;
import com.dentify.domain.pay.model.Pay;
import com.dentify.integration.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppointmentScheduler {

    private final IAppointmentService appointmentService;
    private final INotificationService notificationService;
    private final IPatientStatService statService;
    private final EmailService emailService;


    /**
     * Enviar solicitud de confirmacion 48hs antes,
     * Ejecuta cada 3 horas
     */
    @Scheduled(cron = "0 0 */3 * * ?")
    public void sendConfirmationRemindersTwoDaysBefore() {

        log.info("🔔 Ejecutando solicitudes de confirmacion 48hs antes");

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime twoDaysLater = today.plusDays(2);

        //Look for appointments that are scheduled, not confirmed, within today's date and the next two days.
        List<Appointment> appointments = findReservedAppointmentsNotConfirmed(today, twoDaysLater);

        for (Appointment appointment : appointments) {

            notificationService.createReminderIfNotExists( appointment, ReminderWindow.TWO_DAYS )
                               .ifPresent(notification -> {

                                    try {
                                        emailService.sendConfirmationRequest(notification);

                                        notificationService.registerAttempt(notification, true, null);

                                    } catch (Exception e) {
                                        notificationService.registerAttempt(notification, false, e.getMessage());
                                    }
                                });
        }
    }

    /**
     * Enviar solicitud de confirmacion 24hs antes,
     * Ejecuta cada 3 horas
     */
    @Scheduled(cron = "0 0 */3 * * ?")
    public void sendConfirmationRemindersOneDayBefore() {

        log.info("🔔 Ejecutando solicitudes de confirmacion 24hs antes");

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime oneDayLater = today.plusDays(1);

        //Look for appointments that are scheduled, not confirmed, within today's date and the next two days.
        List<Appointment> appointments = findReservedAppointmentsNotConfirmed(today, oneDayLater);

        for (Appointment appointment : appointments) {

            notificationService.createReminderIfNotExists( appointment, ReminderWindow.ONE_DAY )
                               .ifPresent(notification -> {

                                    try {
                                        emailService.urgentConfirmationRequest(notification);

                                        notificationService.registerAttempt(notification, true, null);

                                    } catch (Exception e) {
                                        notificationService.registerAttempt(notification, false, e.getMessage());
                                    }
                                });
        }
    }

    /**
     * Verifica turnos no confirmados y cancela turnos si es necesario,
     * Ejecuta cada hora
     */
    @Scheduled(cron = "0 0 * * * ?")  // Cada hora
    @Transactional
    public void cancelUnconfirmedAppointments() {
        log.info("Verificando turnos no confirmados");

        LocalTime now = LocalTime.now();
        LocalTime next24Hours = now.plusHours(24);

        //Search for scheduled appointments within the next 24 hours
        List<Appointment> upcomingAppointments = appointmentService.findScheduledAppointmentsBetween(now, next24Hours);

        for (Appointment appointment : upcomingAppointments) {

            if (appointment.getAttendanceConfirmed() == false) {

                this.processAppointmentsWithLackOfConfirmation(appointment);
            }
        }
    }

    private void processAppointmentsWithLackOfConfirmation(Appointment appointment) {

        long remainingHours = calculateRemainingHours( appointment );

        log.warn(" Appointment {} without payment - {} hours remaining", appointment.getId_appointment(), remainingHours);

        // tengo que implementar que cuando se registre un appointment -> se envia email con link de mercado pago a pagar)

        // 3 * 60 = 180
        if ( remainingHours <= 180 ) {

            log.error("Appointment {} cancellation due to non-payment", appointment.getId_appointment());

            appointmentService.cancelAppointment(AppointmentStatus.CANCELLED_BY_SYSTEM, appointment, "Payment not confirmed on time");

            try {
                emailService.sendAppointmentCancelledBySystem(appointment);
                // whatsAppService.sendAppointmentCancelledBySystem(appointment);
            } catch (Exception e) {
                log.error("Error sending cancellation notification: {}", e.getMessage());
            }
        }
    }

    /**
     * Enviar recordatorio final si faltan menos de 3 horas para la cita,
     * Ejecuta cada hora
     */
    @Scheduled(cron = "0 0 * * * ?")  // Cada hora
    @Transactional
    public void sendFinalReminders() {

        log.info("🔔 Ejecutando recordatorio final de 5 horas");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fourHoursLater = now.plusHours(5);

        //Look for unconfirmed appointments within 3 to 4 hours from now
        List<Appointment> appointments = findReservedAppointmentsNotConfirmed( now, fourHoursLater);

        for (Appointment appointment : appointments) {

            processFinalAppointmentReminder( appointment, ReminderWindow.FIVE_HOURS);
        }
    }

    private void processFinalAppointmentReminder(Appointment appointment, ReminderWindow window) {

        //This is the last opportunity to confirm the appointment

        Pay pay = appointment.getPrimaryPayment();

        if (pay == null) {
            log.warn("⚠️ Turno {} sin pago asociado", appointment.getId_appointment());
            return;
        }
        try {
            if ( pay.getPayment_method() == PaymentMethod.MERCADO_PAGO ){

                //Appointment with pending MP -> Email with confirmation button + payment link
                emailService.sendFinalConfirmationRemindingWithMercadoPago(appointment, pay, window);

            }else if (pay.getPayment_method() == PaymentMethod.CASH ){

                //Appointment with pending CASH -> Email with confirmation button only
                emailService.sendFinalConfirmationReminder(appointment, window);
            }

            log.info("Reminder sent for appointment {} (window:{})", appointment.getId_appointment(), window);

        } catch (Exception e) {
            log.error("Error sending appointment reminder {}: {}",
                    appointment.getId_appointment(), e.getMessage());
        }
    }

    private long calculateRemainingHours(Appointment appointment) {

        LocalDateTime appointmentTime = LocalDateTime.of( appointment.getDate(), appointment.getStartTime() );

        LocalDateTime now = LocalDateTime.now();

        return Duration.between( now, appointmentTime ).toHours();
    }

    /**
     * Marca como NO_SHOW los turnos que ya pasaron
     * Ejecuta cada 30 minutos
     */
    @Scheduled(cron = "0 */30 * * * ?")
    @Transactional
    public void markNoShowsAndUpdatePatientStats() {

        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        LocalDate today = LocalDate.now();

        List<Appointment> expiredAppointments = appointmentService.findByDateLessThanEqualAndAppointmentStatusIn(
                                                                     today,
                                                                     List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED) );

        for (Appointment appointment : expiredAppointments) {

            LocalDateTime appointmentDateTime = appointment.getDate().atTime(appointment.getStartTime());

            //If an hour has already passed and the appointment start time is before that time -> markNoShow
            if ( appointmentDateTime.isBefore( oneHourAgo )  ) {

                if ( appointment.getAppointmentStatus() != AppointmentStatus.NO_SHOW){

                        appointmentService.markNoShow(appointment);

                        PatientStat patientStat = statService.actualizeStatsFromPatient( appointment, appointment.getPatient());

                        log.info("Paciente marcado como NO_SHOW | AppointmentID: {} | PatientID: {} | RiskLevel: {}", appointment.getId_appointment(),
                                                                                                                      appointment.getPatient().getId_patient(),
                                                                                                                      patientStat.getRisk_level());

                        this.categorizeWarningToSend(patientStat, appointment);
                    }
                }
            }
        }

    private void categorizeWarningToSend(PatientStat patientStat, Appointment appointment) {

        if( patientStat.getTotal_no_shows() == 1){
            emailService.sendFirstNoShowWarning( appointment);
        }
        if ( patientStat.getNo_shows_last_30_days() == 2){
            emailService.sendSecondNoShowWarning( appointment);
        }
        if ( patientStat.getNo_shows_last_30_days() >= 3){
            emailService.sendThirdNoShowWarning( appointment);
        }
    }

    /**
     * auxiliar method
     * */
    private List<Appointment> findReservedAppointmentsNotConfirmed(LocalDateTime today, LocalDateTime targetDate) {

        return appointmentService.findReservedAppointmentsNotConfirmed(today, targetDate);
    }

    private List<Appointment> findReservedAppointmentsConfirmed(LocalTime startTime, LocalTime finalTime) {

        return appointmentService.findReservedAppointmentsConfirmed( startTime, finalTime);
    }
}