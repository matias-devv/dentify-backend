package com.dentify.integration.whatsapp;

import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.mercadopagopayment.model.MercadoPagoPayment;
import com.dentify.domain.pay.model.Pay;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class WhatsAppService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.whatsapp-from}")
    private String fromNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public void sendPaymentConfirmation(Appointment appointment, Pay pay) {

        String phoneNumber = appointment.getPatient().getPhone_number();

        String patientName = appointment.getPatient().getName();

        String message = String.format(
                """
                        🎉 ¡Hola %s! Tu pago de $%.2f fue confirmado.
                        
                        ✅ Turno confirmado:
                        📅 Fecha: %s
                        🕐 Hora: %s
                        👨‍⚕️ Dr/a: %s
                        
                        Te esperamos!""",
                patientName,
                pay.getAmount(),
                appointment.getDate().format( DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                appointment.getStartTime().format( DateTimeFormatter.ofPattern("HH:mm")),
                appointment.getApp_user().getName()
        );

        sendMessage( phoneNumber, message);
    }

    public void sendPaymentCodeInstructions(Appointment appointment, MercadoPagoPayment payMP) {

        String phoneNumber = appointment.getPatient().getPhone_number();

        String patientName = appointment.getPatient().getName();

        String message = String.format(
                """
                        📋 Hola %s, generaste un código de pago para RapiPago/PagoFácil.
                        
                        💵 Monto: $%.2f
                        ⏰ Tenés 3 días para pagarlo.
                        
                        Una vez que pagues, tu turno quedará confirmado automáticamente.""",
                patientName,
                payMP.getTransactionAmount()
        );

        sendMessage( phoneNumber, message);
    }

    public void sendPaymentRejectedNotification(Appointment appointment, MercadoPagoPayment payMP) {

        String phoneNumber = appointment.getPatient().getPhone_number();

        String patientName = appointment.getPatient().getName();

        String message = String.format(
                """
                        ⚠️ Hola %s, tu pago fue rechazado.
                        
                        Motivo: %s
                        
                        Por favor, intentá nuevamente con otro medio de pago o contactanos.""",
                patientName,
                getHumanReadableError(payMP.getStatusDetail())
        );

        sendMessage( phoneNumber, message);
    }

    private void sendMessage(String phoneNumber, String message) {

        try {
            // Formato: +54 + código de área + número
            String toNumber = "whatsapp:+54" + phoneNumber;

            Message twilioMessage = Message.creator(
                    new PhoneNumber(toNumber),
                    new PhoneNumber(fromNumber),
                    message
            ).create();

            log.info("📱 WhatsApp enviado a {} - SID: {}", phoneNumber, twilioMessage.getSid());

        } catch (Exception e) {
            log.error("❌ Error enviando WhatsApp a {}", phoneNumber, e);
            throw new RuntimeException("Error enviando WhatsApp", e);
        }
    }

    private String getHumanReadableError(String statusDetail) {
        if (statusDetail == null) return "Error desconocido";

        return switch (statusDetail) {
            case "cc_rejected_insufficient_amount" -> "Fondos insuficientes";
            case "cc_rejected_bad_filled_card_number" -> "Número de tarjeta inválido";
            case "cc_rejected_bad_filled_security_code" -> "Código de seguridad inválido";
            case "cc_rejected_call_for_authorize" -> "Rechazada por el banco";
            default -> statusDetail;
        };
    }

    /**
     * Notificación de cancelación automática por falta de pago
     */
    public void sendAppointmentCancelledBySystem(Appointment appointment) {

        String celular = appointment.getPatient().getPhone_number();

        String nombre = appointment.getPatient().getName();

        String mensaje = String.format(
                """
                        Hola %s
                        
                        Lamentablemente tu turno del %s a las %s fue cancelado por falta de pago.
                        
                        Si querés reagendar, contactanos.""",
                nombre,
                appointment.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                appointment.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"))
        );

        sendMessage(celular, mensaje);
    }

    /**
     * Recordatorio del día anterior
     */
    public void sendAppointmentTomorrowReminder(Appointment appointment) {

        String celular = appointment.getPatient().getPhone_number();
        String nombre = appointment.getPatient().getName();

        String mensaje = String.format(
                """
                        👋 Hola %s
                        
                        Te recordamos tu turno confirmado:
                        
                        📅 Mañana %s
                        🕐 %s hs
                        👨‍⚕️ Dr/a %s
                        📍 %s
                        
                        Te esperamos!""",
                nombre,
                appointment.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                appointment.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                appointment.getApp_user().getName(),
                "Dirección de la clínica" // aggregate real direction
        );

        sendMessage(celular, mensaje);
    }

    public void sendUrgentPaymentReminder(Appointment appointment, Pay pay) {

        String celular = appointment.getPatient().getPhone_number();
        String nombre = appointment.getPatient().getName();

        String mensaje = String.format(
                """
                        🚨 URGENTE - Hola %s
                        
                        Tu turno para el %s a las %s está en riesgo de cancelación.
                        
                        ⚠️ No detectamos tu pago todavía.
                        💳 Link de pago: %s
                        
                        Si ya pagaste, ignorá este mensaje.""",
                nombre,
                appointment.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                appointment.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                pay.getMercado_pago_data() != null ? pay.getMercado_pago_data().getInitPoint() : "[link]"
        );

        sendMessage(celular, mensaje);
    }
    }