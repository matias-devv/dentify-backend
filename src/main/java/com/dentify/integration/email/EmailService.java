package com.dentify.integration.email;

import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.mercadopagopayment.model.MercadoPagoPayment;
import com.dentify.domain.notification.enums.PaymentNotificationConfig;
import com.dentify.domain.notification.model.Notification;
import com.dentify.domain.notification.service.INotificationService;
import com.dentify.domain.pay.enums.PaymentMethod;
import com.dentify.domain.pay.enums.PaymentStatus;
import com.dentify.domain.notification.enums.ReminderWindow;
import com.dentify.domain.patient.model.Patient;
import com.dentify.domain.pay.model.Pay;
import com.dentify.domain.receipt.model.Receipt;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.InputStream;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${notifications.email.enabled:true}")
    private boolean emailEnabled;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${clinic.logo.url:}")
    private String clinicLogoUrl; // optional

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final INotificationService notificationService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Notificación de turno cancelado por el odontólogo
     */
    public void sendAppointmentCancelledByDentist(Appointment appointment) {
        if (!emailEnabled) return;

        try {
            String patientEmail = appointment.getPatient().getEmail();

            if (patientEmail == null || patientEmail.isBlank()) {
                log.warn("Patient without email - Appointment: {}", appointment.getId_appointment());
                return;
            }

            Map<String, Object> data = buildBaseEmailData(appointment);

            // Agregar motivo de cancelación si existe
            data.put("cancellationReason", appointment.getReason_for_cancellation() != null
                    ? appointment.getReason_for_cancellation()
                    : "El odontólogo tuvo que cancelar este turno por motivos de agenda.");

            sendEmail(
                    patientEmail,
                    "Turno cancelado - " + appointment.getApp_user().getClinic_name(),
                    "email/appointment-cancelled-by-dentist",
                    data
            );

            log.info("Cancellation email sent to: {}", patientEmail);

        } catch (Exception e) {
            log.error("Error sending cancellation email: {}", e.getMessage());
        }
    }

    /**
     * Notificación de turno cancelado por falta de pago
     */
    public void sendAppointmentCancelledBySystem(Appointment appointment) {
        if (!emailEnabled) return;

        try {

           String patientEmail = appointment.getPatient().getEmail();

            if (patientEmail == null || patientEmail.isBlank()) {
                log.warn("Patient without email - Appointment: {}", appointment.getId_appointment());
                return;
            }

            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("paciente", appointment.getPatient().getName());
            templateModel.put("fecha", appointment.getDate().format(DATE_FORMATTER));
            templateModel.put("hora", appointment.getStartTime().format(TIME_FORMATTER));

            sendEmail(
                    patientEmail,
                    "❌ Turno cancelado por falta de pago",
                    "email/appointment-cancelled",
                    templateModel
            );

            log.info("Email of cancellation sending to: {}", patientEmail);

        } catch (Exception e) {
            log.error("Error sending email of cancellation sending to {}", e.getMessage());
        }
    }

    public void sendConfirmationRequest(Notification notification) {

        Appointment appointment = notification.getAppointment();
        PaymentMethod paymentMethod = appointment.getPrimaryPaymentMethod();

        if (paymentMethod == PaymentMethod.MERCADO_PAGO) {
            sendConfirmationWithPaymentOption(notification, appointment);
        } else {
            sendConfirmationCashOnly(notification, appointment);
        }
    }

    /**
     * Confirmación con Mercado Pago (48hs antes)
     */
    private void sendConfirmationWithPaymentOption(Notification notification, Appointment appointment) {
        try {

            String patientEmail = appointment.getPatient().getEmail();

            if (patientEmail == null || patientEmail.isBlank()) {

                log.warn("Patient without email - Appointment: {}", appointment.getId_appointment());
                return;
            }

            Pay payment = appointment.getPrimaryPayment();

            // ✅ Usar el builder base
            Map<String, Object> data = buildBaseEmailData(appointment);
            addPaymentData(data, payment);

            // Añadir datos específicos
            data.put("confirmationUrl", buildConfirmationUrl(notification));

            if (payment.getMercado_pago_data() != null) {
                addMercadoPagoData(data, payment.getMercado_pago_data());
            }

            sendEmail(
                    patientEmail,
                    "🔔 Confirmá tu asistencia - Turno en 2 días",
                    "email/confirmation-request-mercadopago-48h",
                    data
            );

            log.info("Confirmation email (MERCADO_PAGO) sent to: {}", patientEmail);

        } catch (Exception e) {
            log.error("Error sending confirmation email (MERCADO_PAGO): {}", e.getMessage());
            throw new RuntimeException("Error sending confirmation email (MERCADO_PAGO)", e);
        }
    }

    /**
     * Confirmación solo efectivo (48hs antes)
     */
    private void sendConfirmationCashOnly(Notification notification, Appointment appointment) {
        try {
            String patientEmail = appointment.getPatient().getEmail();

            if (patientEmail == null || patientEmail.isBlank()) {
                log.warn("Patient without email - Appointment: {}", appointment.getId_appointment());
                return;
            }

            Pay payment = appointment.getPrimaryPayment();

            // ✅ Usar el builder base
            Map<String, Object> data = buildBaseEmailData(appointment);
            addPaymentData(data, payment);
            data.put("confirmationUrl", buildConfirmationUrl(notification));

            sendEmail(
                    patientEmail,
                    "🔔 Confirmá tu asistencia - Turno en 2 días",
                    "email/confirmation-request-cash-48h",
                    data
            );

            log.info("Confirmation email (CASH) sent to: {}", patientEmail);

        } catch (Exception e) {
            log.error("Error sending confirmation email (CASH): {}", e.getMessage());
            throw new RuntimeException("Error sending confirmation email (CASH)", e);
        }
    }

    public String buildConfirmationUrl(Notification notification) {
        return String.format("%s/public/appointments/confirm/%d/%s",
                baseUrl,
                notification.getAppointment().getId_appointment(),
                notification.getConfirmation_token());
    }

    public void urgentConfirmationRequest(Notification notification) {

        Appointment appointment = notification.getAppointment();
        PaymentMethod payment = appointment.getPrimaryPaymentMethod();

        if ( payment == PaymentMethod.MERCADO_PAGO) {

            sendUrgentConfirmationWithPaymentOption( notification, appointment, payment);
        } else {

            sendUrgentConfirmationCashOnly( notification, appointment);
        }
    }

    /**
     * Recordatorio urgente para turno con MERCADO PAGO
     * Se envía 24hs antes del turno
     */
    private void sendUrgentConfirmationWithPaymentOption(Notification notification, Appointment appointment, PaymentMethod payment) {
        try {
            String patientEmail = appointment.getPatient().getEmail();

            if (patientEmail == null || patientEmail.isBlank()) {

                log.warn("⚠️ Paciente sin email - Turno: {}", appointment.getId_appointment());
                return;
            }

            Pay pay = appointment.getPrimaryPayment();

            // ✅ Usar el builder base
            Map<String, Object> data = buildBaseEmailData(appointment);
            addPaymentData(data, pay);

            // Añadir datos específicos de confirmación urgente
            data.put("confirmationUrl", buildConfirmationUrl(notification));

            // Añadir datos de MercadoPago si existen
            if (pay.getMercado_pago_data() != null) {
                addMercadoPagoData(data, pay.getMercado_pago_data());
            }

            sendEmail(
                    patientEmail,
                    "🚨 URGENTE - Confirmá tu turno o se cancelará",
                    "email/urgent-confirmation-request-mercadopago-24h",
                    data
            );

            log.info("URGENT confirmation email (MERCADO_PAGO) sent to: {}", patientEmail);

        } catch (Exception e) {
            log.error("Error sending urgent confirmation email (MERCADO_PAGO): {}", e.getMessage());
            throw new RuntimeException("Error sending urgent confirmation email (MERCADO_PAGO)", e);
        }
    }

    /**
     * Recordatorio urgente para turno con EFECTIVO
     * Se envía 24hs antes del turno
     */
    private void sendUrgentConfirmationCashOnly(Notification notification, Appointment appointment) {
        try {
            String patientEmail = appointment.getPatient().getEmail();

            if (patientEmail == null || patientEmail.isBlank()) {
                log.warn("⚠️ Paciente sin email - Turno: {}", appointment.getId_appointment());
                return;
            }

            Pay payment = appointment.getPrimaryPayment();

            // ✅ Usar el builder base
            Map<String, Object> data = buildBaseEmailData(appointment);
            addPaymentData(data, payment);

            // Añadir datos específicos de confirmación urgente
            data.put("confirmationUrl", buildConfirmationUrl(notification));

            sendEmail(
                    patientEmail,
                    "🚨 URGENTE - Confirmá tu turno o se cancelará",
                    "email/urgent-confirmation-request-cash-24h",
                    data
            );

            log.info("URGENT confirmation email (CASH) sent to: {}", patientEmail);

        } catch (Exception e) {
            log.error("Error sending urgent confirmation email (CASH): {}", e.getMessage());
            throw new RuntimeException("Error sending urgent confirmation email (CASH)", e);
        }
    }

    /**
     * Recordatorio final para turno con MERCADO_PAGO
     * Se envía 5 horas antes del turno
     */
    public void sendFinalConfirmationRemindingWithMercadoPago(Appointment appointment, Pay pay, ReminderWindow window) {
        try {
            String patientEmail = appointment.getPatient().getEmail();

            if (patientEmail == null || patientEmail.isBlank()) {
                log.warn("⚠️ Paciente sin email - Turno: {}", appointment.getId_appointment());
                return;
            }

            // ✅ Usar el builder base
            Map<String, Object> data = buildBaseEmailData(appointment);
            addPaymentData(data, pay);

            // Añadir datos de MercadoPago si existen
            if (pay.getMercado_pago_data() != null) {
                addMercadoPagoData(data, pay.getMercado_pago_data());
            }

            // Nota: isConfirmed ya está en buildBaseEmailData()
            // Si necesitas añadir dirección en el futuro, descomenta:
            // data.put("direccion", appointment.getApp_user().getAddress() != null
            //         ? appointment.getApp_user().getAddress() : "Consultorio");

            sendEmail(
                    patientEmail,
                    "⏰ Tu turno es hoy - Recordatorio final",
                    "email/final-reminder-mercadopago",
                    data
            );

            log.info("Final reminder (MERCADO_PAGO) sent to: {}", patientEmail);

        } catch (Exception e) {
            log.error("Error sending final reminder (MERCADO_PAGO): {}", e.getMessage());
            throw new RuntimeException("Error sending final reminder (MERCADO_PAGO)", e);
        }
    }

    /**
     * Recordatorio final para turno con pago en EFECTIVO
     * Se envía 5 horas antes del turno
     */
    public void sendFinalConfirmationReminder(Appointment appointment, ReminderWindow window) {
        try {
            String patientEmail = appointment.getPatient().getEmail();

            if (patientEmail == null || patientEmail.isBlank()) {
                log.warn("⚠️ Paciente sin email - Turno: {}", appointment.getId_appointment());
                return;
            }

            Pay payment = appointment.getPrimaryPayment();

            // ✅ Usar el builder base
            Map<String, Object> data = buildBaseEmailData(appointment);
            addPaymentData(data, payment);

            // Nota: isConfirmed ya está en buildBaseEmailData()
            // Si necesitas añadir dirección en el futuro, descomenta:
            // data.put("direccion", appointment.getApp_user().getAddress() != null
            //         ? appointment.getApp_user().getAddress() : "Consultorio");

            sendEmail(
                    patientEmail,
                    "⏰ Tu turno es hoy - Recordatorio final",
                    "email/final-reminder-cash",
                    data
            );

            log.info("Final reminder (CASH) sent to: {}", patientEmail);

        } catch (Exception e) {
            log.error("Error sending final reminder (CASH): {}", e.getMessage());
            throw new RuntimeException("Error sending final reminder (CASH)", e);
        }
    }

    // ========================================================================
    // MÉTODOS PRIVADOS DE COMPROBANTES
    // ========================================================================

    /**
     * Email al paciente y odontólogo cuando se confirma el pago con Efectivo
     */
    public void sendPaymentReceipt(Appointment appointment, Pay pay, Receipt receipt) {

        try {
            //Create all notifications at once
            List<Notification> notifications = Arrays.stream( PaymentNotificationConfig.values())
                    .map(config -> notificationService.buildNotification(
                            appointment,
                            config.getType(),
                            config.getChannel()
                    ))
                    .collect(Collectors.toList());

            //Persist all
            notificationService.saveAll(notifications);

            // Enviar emails
            sendCashReceiptToThePatient(appointment, pay, receipt);
            sendAppointmentConfirmationToThePatient(appointment);

            sendCashReceiptToTheDentist(appointment, pay, receipt);
            sendAppointmentConfirmationToTheDentist(appointment);

        } catch (Exception e) {
            log.error("Error sending payment receipts: {}", e.getMessage());
        }
    }

    /**
     * Email al paciente y odontólogo cuando se confirma el pago via Mercado Pago
     */
    public void sendPaymentReceiptMercadoPago(Appointment appointment, Pay pay, MercadoPagoPayment payMP, Receipt receipt) {

        try {
            //Create all notifications at once
            List<Notification> notifications = Arrays.stream( PaymentNotificationConfig.values())
                    .map(config -> notificationService.buildNotification(
                            appointment,
                            config.getType(),
                            config.getChannel()
                    ))
                    .collect(Collectors.toList());

            //Persist all
            notificationService.saveAll(notifications);

            // Enviar emails
            sendMercadoPagoReceiptToThePatient(appointment, pay, payMP, receipt);
            sendAppointmentConfirmationToThePatient(appointment);

            sendMercadoPagoReceiptToTheDentist(appointment, pay, payMP, receipt);
            sendAppointmentConfirmationToTheDentist(appointment);

        } catch (Exception e) {
            log.error("Error sending payment receipts: {}", e.getMessage());
        }
    }

    /**
     * Enviar comprobante de pago en EFECTIVO al PACIENTE (con PDF adjunto)
     */
    public void sendCashReceiptToThePatient(Appointment appointment, Pay pay, Receipt receipt) {
        if (!emailEnabled) {
            log.info("Email notifications disabled - skipping cash receipt to patient");
            return;
        }

        try {
            Map<String, Object> data = buildBaseEmailData(appointment);
            addPaymentData(data, pay);
            addReceiptData(data, receipt);
            data.put("isPatient", true);
            data.put("paymentReceivedAt", "Consultorio"); // O desde configuración si tienes dirección

            sendEmailWithAttachment(
                    appointment.getPatient().getEmail(),
                    "✅ Comprobante de Pago en Efectivo - " + appointment.getApp_user().getClinic_name(),
                    "payment-receipt-cash-patient",
                    data,
                    receipt
            );

            log.info("Cash receipt sent to patient: {}", appointment.getPatient().getEmail());

        } catch (Exception e) {
            log.error("Error sending cash receipt to patient", e);
            throw new RuntimeException("Failed to send cash receipt to patient", e);
        }
    }

    /**
     * Enviar comprobante de pago en EFECTIVO al DENTISTA (con PDF adjunto)
     */
    public void sendCashReceiptToTheDentist(Appointment appointment, Pay pay, Receipt receipt) {
        if (!emailEnabled) {
            log.info("Email notifications disabled - skipping cash receipt to dentist");
            return;
        }

        try {
            Map<String, Object> data = buildBaseEmailData(appointment);
            addPaymentData(data, pay);
            addReceiptData(data, receipt);
            data.put("isPatient", false);
            data.put("paymentReceivedAt", "Consultorio");

            sendEmailWithAttachment(
                    appointment.getApp_user().getEmail(),
                    "💵 Pago en Efectivo Recibido - Paciente: " + appointment.getPatient().getName(),
                    "payment-receipt-cash-dentist",
                    data,
                    receipt
            );

            log.info("Cash receipt sent to dentist: {}", appointment.getApp_user().getEmail());

        } catch (Exception e) {
            log.error("Error sending cash receipt to dentist", e);
            throw new RuntimeException("Failed to send cash receipt to dentist", e);
        }
    }

    // ==================== MÉTODOS PÚBLICOS DE ENVÍO ====================

    /**
     * 1. Enviar comprobante de pago al PACIENTE (con PDF)
     */
    public void sendMercadoPagoReceiptToThePatient(Appointment appointment, Pay pay, MercadoPagoPayment payMP, Receipt receipt) {
        if (!emailEnabled) {
            log.info("Email notifications disabled - skipping receipt to patient");
            return;
        }

        try {
            Map<String, Object> data = buildBaseEmailData(appointment);
            addPaymentData(data, pay);
            addMercadoPagoData(data, payMP);
            addReceiptData(data, receipt);
            data.put("isPatient", true);

            sendEmailWithAttachment(
                    appointment.getPatient().getEmail(),
                    "✅ Comprobante de Pago - " + appointment.getApp_user().getClinic_name(),
                    "payment-receipt-patient",
                    data,
                    receipt
            );

            log.info("Payment receipt sent to patient: {}", appointment.getPatient().getEmail());

        } catch (Exception e) {
            log.error("Error sending payment receipt to patient", e);
            throw new RuntimeException("Failed to send payment receipt to patient", e);
        }
    }

    /**
     * 2. Enviar confirmación de turno al PACIENTE
     */
    public void sendAppointmentConfirmationToThePatient(Appointment appointment) {
        if (!emailEnabled) {
            log.info("Email notifications disabled - skipping confirmation to patient");
            return;
        }

        try {
            Map<String, Object> data = buildBaseEmailData(appointment);
            data.put("isPatient", true);

            sendEmail(
                    appointment.getPatient().getEmail(),
                    "🗓️ Turno Confirmado - " + appointment.getApp_user().getClinic_name(),
                    "appointment-confirmation-patient",
                    data
            );

            log.info("Appointment confirmation sent to patient: {}", appointment.getPatient().getEmail());

        } catch (Exception e) {
            log.error("Error sending appointment confirmation to patient", e);
            throw new RuntimeException("Failed to send appointment confirmation to patient", e);
        }
    }

    /**
     * 3. Enviar comprobante de pago al DENTISTA (con PDF)
     */
    public void sendMercadoPagoReceiptToTheDentist(Appointment appointment, Pay pay, MercadoPagoPayment payMP, Receipt receipt) {
        if (!emailEnabled) {
            log.info("Email notifications disabled - skipping receipt to dentist");
            return;
        }

        try {
            Map<String, Object> data = buildBaseEmailData(appointment);
            addPaymentData(data, pay);
            addMercadoPagoData(data, payMP);
            addReceiptData(data, receipt);
            data.put("isPatient", false);

            sendEmailWithAttachment(
                    appointment.getApp_user().getEmail(),
                    "💰 Nuevo Pago Recibido - Paciente: " + appointment.getPatient().getName(),
                    "payment-receipt-dentist",
                    data,
                    receipt
            );

            log.info("Payment receipt sent to dentist: {}", appointment.getApp_user().getEmail());

        } catch (Exception e) {
            log.error("Error sending payment receipt to dentist", e);
            throw new RuntimeException("Failed to send payment receipt to dentist", e);
        }
    }

    /**
     * 4. Enviar confirmación de turno al DENTISTA
     */
    public void sendAppointmentConfirmationToTheDentist(Appointment appointment) {
        if (!emailEnabled) {
            log.info("Email notifications disabled - skipping confirmation to dentist");
            return;
        }

        try {
            Map<String, Object> data = buildBaseEmailData(appointment);
            data.put("isPatient", false);

            sendEmail(
                    appointment.getApp_user().getEmail(),
                    "📅 Turno Confirmado - Paciente: " + appointment.getPatient().getName(),
                    "appointment-confirmation-dentist",
                    data
            );

            log.info("Appointment confirmation sent to dentist: {}", appointment.getApp_user().getEmail());

        } catch (Exception e) {
            log.error("Error sending appointment confirmation to dentist", e);
            throw new RuntimeException("Failed to send appointment confirmation to dentist", e);
        }
    }


    // ========================================================================
    // UTILIDADES
    // ========================================================================

    /**
     *  Private method for sending emails with a Thymeleaf template
    * @param to Recipient's email address

    * @param subject Email subject

    * @param templateName Template name (without .html extension)

    * @param model Template variables
    */
    private void sendEmail(String to, String subject, String templateName, Map<String, Object> model) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("noreply@tuclinica.com"); // O desde configuración

            Context context = new Context( Locale.forLanguageTag("es-AR") );
            context.setVariables(model); //sets all the variables of the Map

            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.debug("Email sent successfully: {} a {}", subject, to);

        } catch (MessagingException e) {
            log.error("Error sending email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Error sending email to", e);
        }
    }
    /**
     * Enviar email CON adjunto PDF
     */
    private void sendEmailWithAttachment(String to, String subject, String templateName,
                                         Map<String, Object> model, Receipt receipt) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);

            Context context = new Context(Locale.forLanguageTag("es-AR"));
            context.setVariables(model);

            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);

            // Adjuntar PDF
            byte[] pdfBytes = downloadPdfFromUrl(receipt.getUrl_pdf());
            helper.addAttachment(receipt.getFilename(), new ByteArrayResource(pdfBytes), "application/pdf");

            mailSender.send(message);

            log.debug("Email with PDF sent successfully: {} to {}", subject, to);

        } catch (Exception e) {
            log.error("Error sending email with attachment to {}: {}", to, e.getMessage());
            throw new RuntimeException("Error sending email with attachment", e);
        }
    }

    /**
     * Descargar PDF desde URL (Cloudinary)
     */
    private byte[] downloadPdfFromUrl(String pdfUrl) throws Exception {
        try (InputStream in = new URL(pdfUrl).openStream()) {
            return in.readAllBytes();
        }
    }


    // ========================================================================
    // RECORDATORIOS DE NO-SHOW
    // ========================================================================

    /**
     * Primer advertencia por turno perdido
     */
    public void sendFirstNoShowWarning(Appointment appointment) {
        try {
            String patientEmail = appointment.getPatient().getEmail();

            if (patientEmail == null || patientEmail.isBlank()) {
                log.warn("Patient without email - Appointment: {}", appointment.getId_appointment());
                return;
            }


            Map<String, Object> data = buildBaseEmailData(appointment);

            sendEmail(
                    patientEmail,
                    "Recordatorio importante sobre tu turno perdido",
                    "email/first-no-show-warning",
                    data
            );

            log.info("First no-show warning sent to: {}", patientEmail);

        } catch (Exception e) {
            log.error("Error sending first no-show warning: {}", e.getMessage());
        }
    }

    /**
     * Segunda advertencia por turno perdido
     */
    public void sendSecondNoShowWarning(Appointment appointment) {
        try {
            String patientEmail = appointment.getPatient().getEmail();

            if (patientEmail == null || patientEmail.isBlank()) {
                log.warn("Patient without email - Appointment: {}", appointment.getId_appointment());
                return;
            }

            Map<String, Object> data = buildBaseEmailData(appointment);

            sendEmail(
                    patientEmail,
                    "Segundo turno perdido - Por favor contáctanos",
                    "email/second-no-show-warning",
                    data
            );

            log.info("Second no-show warning sent to: {}", patientEmail);

        } catch (Exception e) {
            log.error("Error sending second no-show warning: {}", e.getMessage());
        }
    }

    /**
     * Tercera advertencia por turno perdido (última oportunidad)
     */
    public void sendThirdNoShowWarning(Appointment appointment) {
        try {
            String patientEmail = appointment.getPatient().getEmail();

            if (patientEmail == null || patientEmail.isBlank()) {
                log.warn("Patient without email - Appointment: {}", appointment.getId_appointment());
                return;
            }

            Map<String, Object> data = buildBaseEmailData(appointment);

            // Añadir URL de reserva online (si la necesitas en el template)
            data.put("onlineBookingUrl", baseUrl + "/turnos");

            sendEmail(
                    patientEmail,
                    "Acción requerida - Múltiples turnos perdidos",
                    "email/third-no-show-warning",
                    data
            );

            log.info("Third no-show warning sent to: {}", patientEmail);

        } catch (Exception e) {
            log.error("Error sending third no-show warning: {}", e.getMessage());
        }
    }

    // ==================== DATA BUILDERS====================

    /**
     * Construir datos comunes para TODOS los emails
     */
    private Map<String, Object> buildBaseEmailData(Appointment appointment) {
        Map<String, Object> data = new HashMap<>();

        // Clínica
        data.put("clinicName", appointment.getApp_user().getClinic_name());
        data.put("clinicLogoUrl", clinicLogoUrl);
        data.put("clinicEmail", appointment.getApp_user().getEmail());
        data.put("clinicaWhatsApp", appointment.getApp_user().getPhone_number());

        // Paciente
        data.put("paciente", appointment.getPatient().getName()); // nombre corto
        data.put("patientName", appointment.getPatient().getName() + " " + appointment.getPatient().getSurname());
        data.put("patientEmail", appointment.getPatient().getEmail());
        data.put("patientPhone", appointment.getPatient().getPhone_number());
        data.put("patientDni", appointment.getPatient().getDni());

        // Dentista
        data.put("dentista", appointment.getApp_user().getName()); // nombre corto
        data.put("dentistName", appointment.getApp_user().getName() + " " + appointment.getApp_user().getSurname());
        data.put("dentistEmail", appointment.getApp_user().getEmail());
        data.put("dentistPhone", appointment.getApp_user().getPhone_number());

        // Turno
        data.put("appointmentId", appointment.getId_appointment());
        data.put("fecha", appointment.getDate().format(DATE_FORMATTER));
        data.put("hora", appointment.getStartTime().format(TIME_FORMATTER));
        data.put("appointmentDate", appointment.getDate().format(DATE_FORMATTER));
        data.put("appointmentTime", appointment.getStartTime().format(TIME_FORMATTER));
        data.put("duration", appointment.getDuration_minutes() + " minutos");

        // Tratamiento
        String treatmentName = (appointment.getTreatment() != null && appointment.getTreatment().getProduct() != null)
                ? appointment.getTreatment().getProduct().getName_product()
                : "Consulta general";
        data.put("treatmentName", treatmentName);

        // Instrucciones
        data.put("patientInstructions", appointment.getPatient_instructions() != null
                ? appointment.getPatient_instructions()
                : "Llegar 10 minutos antes de la hora del turno.");

        // Estado
        data.put("isConfirmed", appointment.getAttendanceConfirmed());
        data.put("status", "CONFIRMADO");

        // Footer
        data.put("currentYear", java.time.Year.now().getValue());
        data.put("baseUrl", baseUrl);

        return data;
    }

    /**
     * Añadir datos de pago a un Map existente
     */
    private void addPaymentData(Map<String, Object> data, Pay pay) {
        data.put("monto", pay.getAmount());
        data.put("paymentAmount", String.format("$%.2f", pay.getAmount()));
        data.put("paymentMethod", translatePaymentMethod(pay.getPayment_method().name()));
        data.put("paymentStatus", pay.getPayment_status() == PaymentStatus.PAID ? "Aprobado" : "Pendiente");
        data.put("isPaid", pay.getPayment_status() == PaymentStatus.PAID);
    }

    /**
     * Añadir datos de MercadoPago a un Map existente
     */
    private void addMercadoPagoData(Map<String, Object> data, MercadoPagoPayment payMP) {
        if (payMP == null) return;

        data.put("mpPaymentId", payMP.getPaymentId() != null ? payMP.getPaymentId().toString() : "N/A");
        data.put("mpExternalReference", payMP.getExternalReference());
        data.put("mpDateApproved", payMP.getDateApproved() != null ? payMP.getDateApproved().format(DATETIME_FORMATTER) : "N/A");
        data.put("mpInstallments", payMP.getInstallments() != null && payMP.getInstallments() > 1 ? payMP.getInstallments() + " cuotas" : "Pago único");

        // Link de pago
        String paymentLink = (payMP.getInitPoint() != null) ? payMP.getInitPoint() : null;
        data.put("paymentLink", paymentLink);
    }

    /**
     * Añadir datos del recibo
     */
    private void addReceiptData(Map<String, Object> data, Receipt receipt) {
        data.put("receiptNumber", receipt.getFilename());
    }

    /**
     * Traducir método de pago
     */
    private String translatePaymentMethod(String method) {
        return switch (method) {
            case "CASH" -> "Efectivo";
            case "CREDIT_CARD" -> "Tarjeta de Crédito";
            case "DEBIT_CARD" -> "Tarjeta de Débito";
            case "MERCADO_PAGO" -> "Mercado Pago";
            default -> method;
        };
    }

}