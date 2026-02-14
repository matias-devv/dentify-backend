package com.dentify.integration.mercadopago;

import com.dentify.domain.appointment.enums.AppointmentStatus;
import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.appointment.service.IAppointmentService;
import com.dentify.domain.notification.service.INotificationService;
import com.dentify.integration.email.EmailService;
import com.dentify.domain.mercadopagopayment.model.MercadoPagoPayment;
import com.dentify.domain.mercadopagopayment.service.IMercadoPagoPaymentService;
import com.dentify.domain.pay.model.Pay;
import com.dentify.domain.pay.service.IPayService;
import com.dentify.domain.receipt.model.Receipt;
import com.dentify.domain.receipt.service.IReceiptService;
import com.dentify.domain.treatment.model.Treatment;
import com.dentify.domain.treatment.service.ITreatmentService;
import com.mercadopago.resources.payment.Payment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentProcessorService {

    private final IMercadoPagoPaymentService mercadoPagoPaymentService;
    private final IPayService payService;
    private final IAppointmentService appointmentService;
    private final ITreatmentService treatmentService;

    private final IReceiptService receiptService;
    private final EmailService emailService;

    @Transactional
    public void handlePaymentNotification(Payment payment) {

        log.info("🔔 Procesando notificación de pago");
        log.info("  - Payment ID: {}", payment.getId());
        log.info("  - Status: {}", payment.getStatus());
        log.info("  - External Reference: {}", payment.getExternalReference());

        //get the payment in DB through the external reference
        String externalRef = payment.getExternalReference();

        if (externalRef == null) {
            //payment without external reference
            return;
        }

        Optional<MercadoPagoPayment> optionalPayMP = mercadoPagoPaymentService.findByExternalReference(externalRef);

        if ( optionalPayMP.isEmpty() ) {
            log.error("payment not found in database: {}", externalRef);
            return;
        }

        MercadoPagoPayment payMP = optionalPayMP.get();

        Pay pay = payMP.getPay();

        Appointment appointment = pay.getAppointment();

        Treatment treatment = pay.getTreatment();

        //update pay mercado pago data
        mercadoPagoPaymentService.updatePayMercadoPagoData( payMP, payment);

        //process according to state
        String status = payment.getStatus();

        switch (status) {
            case "approved":
                handleApprovedPayment(pay, appointment, treatment, payMP);
                break;

            case "pending":
                handlePendingPayment(pay, appointment, payMP);
                break;

            case "rejected":
            case "cancelled":
                handleFailedPayment(pay, appointment, payMP);
                break;

            case "refunded":
            case "charged_back":
                handleRefundedPayment(pay, appointment, treatment, payMP);
                break;

            default:
                log.warn(" Estado desconocido: {}", status);
        }
    }

    private void handleApprovedPayment(Pay pay, Appointment appointment,
                                       Treatment treatment, MercadoPagoPayment payMP) {

        //actualize pay status
        payService.actualizePaymentStatusToPaidAndSetActualDate(pay);

        //actualize outstanding balance
        treatmentService.actualizeTreatmentOutstandingBalanceAndSetStatusInProgress(treatment, pay);

        //appointment -> CONFIRMED
        appointmentService.actualizeAppointmentStatusToConfirmed(appointment);

        //generate receipt and send email
        try {

            Receipt receipt = receiptService.generateAndSaveReceipt(pay, appointment, payMP);

            emailService.sendPaymentReceiptMercadoPago( appointment, pay, payMP, receipt );

            log.info("MercadoPago receipt generated and sent: {}", receipt.getFilename());
        } catch (Exception e) {
            log.error("Error generating/sending MercadoPago receipt", e);
            // Loggear pero no romper el webhook
        }
    }

    private void handlePendingPayment(Pay pay, Appointment appointment,
                                      MercadoPagoPayment payMP) {

        //actualize pay status
        payService.actualizePaymentStatusToAwaitingPayment(pay);

        //If it's RapiPago/PagoFácil, send instructions
        if ( "ticket".equals( payMP.getPaymentTypeId() ) ) {
            try {
                //whatsAppService.sendPaymentCodeInstructions(appointment, payMP);
                log.warn("⚠️ WhatsApp service not implemented - skipping notification");
            }catch (Exception e) {
                throw new RuntimeException("error sending instructions", e);
            }
        }
    }

    private void handleFailedPayment(Pay pay, Appointment appointment,
                                     MercadoPagoPayment payMP) {

        //actualize payment status
        payService.actualizePaymentStatusToFailedPayment(pay);

        try {
            //notify the patient to retry
            //whatsAppService.sendPaymentRejectedNotification(appointment, payMP);
            log.warn("⚠️ WhatsApp service not implemented - skipping notification");
        } catch (Exception e) {
            throw new RuntimeException("error sending notification", e);
        }
    }

    private void handleRefundedPayment(Pay pay, Appointment appointment,
                                       Treatment treatment, MercadoPagoPayment payMP) {

        //reverse everything
        payService.actualizePaymentStatusToCancelled(pay);

        treatmentService.calculateOutstandingBalanceAfterRefund(treatment, pay);

        //cancel appointment if you have not yet been seen
        if (appointment.getAppointmentStatus() != AppointmentStatus.COMPLETED) {

            appointmentService.cancelAppointment( AppointmentStatus.CANCELLED_BY_SYSTEM, appointment, "contracargo de pago");
        }
    }
}
