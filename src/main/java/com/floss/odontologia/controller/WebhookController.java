package com.floss.odontologia.controller;

import com.floss.odontologia.service.impl.MercadoPagoWebhookService;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@Slf4j
@RequiredArgsConstructor
public class WebhookController {


    private final MercadoPagoWebhookService webhookService;

    /*
      Endpoint that receives notifications from MercadoPago
      MercadoPago sends:
       - Query parameters: ?id=123456&
       - topic=payment
       - Headers: x-signature, x-request-id
     */
    @PostMapping("/mercadopago")
    public ResponseEntity<Void> receiveMercadoPagoNotification(
                                                                @RequestParam(value = "id", required = false) Long paymentId,
                                                                @RequestParam(value = "topic", required = false) String topic,
                                                                @RequestHeader(value = "x-signature", required = false) String signature,
                                                                @RequestHeader(value = "x-request-id", required = false) String requestId,
                                                                @RequestBody(required = false) String rawBody) {


        try {
            // MercadoPago can send different types of notifications
            if ("payment".equals(topic) && paymentId != null) {

                // CRITICAL: Validate signature to prevent fraud
                if (signature != null && requestId != null) {

                    boolean isValid = webhookService.validateSignature(signature, requestId, paymentId);

                    if (!isValid) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                    }
                }

                //process the pay
                log.info("procesando pago");
                webhookService.processPayment(paymentId);

            } else if ("merchant_order".equals(topic)) {
                log.info(" merchant order received (ignored for now)");
            } else {
                log.warn("topic unknown: {}", topic);
            }

            // IMPORTANT: always return 200 OK to MercadoPago
            // Otherwise, it will retry the notification.
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("error procesing the webhook", e);
            // Igual devolvemos 200 para que MP no reintente
            return ResponseEntity.ok().build();
        }
    }
}
