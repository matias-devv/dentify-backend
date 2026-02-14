package com.dentify.integration.mercadopago;

import com.dentify.integration.mercadopago.dto.MercadoPagoWebhookDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class MercadoPagoWebhookController {


    private final MercadoPagoWebhookService webhookService;

    /**
     * Endpoint that receives notifications from MercadoPago
     */
    @PostMapping("/mp/webhook")
    public ResponseEntity<Void> receiveMercadoPagoNotification(@RequestBody(required = false) MercadoPagoWebhookDTO webhook,
                                                               @RequestHeader(value = "x-signature", required = false) String signature,
                                                               @RequestHeader(value = "x-request-id", required = false) String requestId) {

        log.info("===========================================");
        log.info("🔔 WEBHOOK RECIBIDO");
        log.info("===========================================");
        log.info("📦 Webhook body: {}", webhook);
        log.info("🔐 x-signature: {}", signature != null ? "Presente" : "Ausente");
        log.info("🆔 x-request-id: {}", requestId);

        try {
            if (webhook == null) {
                log.warn("⚠️ Webhook body es null");
                return ResponseEntity.ok().build();
            }

            // Extraer datos
            Long paymentId = null;
            String topic = webhook.getType();

            if (webhook.getData() != null && webhook.getData().getId() != null) {
                paymentId = Long.parseLong(webhook.getData().getId());
            }

            log.info("📍 Payment ID: {}", paymentId);
            log.info("📍 Topic: {}", topic);

            // VALIDAR FIRMA (CRÍTICO PARA SEGURIDAD)
            if (signature != null && requestId != null && paymentId != null) {

                boolean isValid = webhookService.validateSignature(signature, requestId, paymentId);

                if (!isValid) {

                    log.error("❌ FIRMA INVÁLIDA - Posible ataque de spoofing");
                    return ResponseEntity.status( HttpStatus.UNAUTHORIZED).build();
                }

                log.info("✅ Firma validada correctamente");

            } else {
                log.warn("⚠️ Sin firma - webhook podría ser de simulación");
            }

            // Procesar el pago
            if ("payment".equals(topic) && paymentId != null) {

                log.info("✅ Procesando pago ID: {}", paymentId);

                webhookService.processPayment(paymentId);

            } else {
                log.warn("⚠️ Topic no es 'payment' o paymentId es null");
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {

            log.error("❌ Error procesando webhook", e);
            return ResponseEntity.ok().build(); // Siempre devolver 200 para evitar reintentos
        }
    }
}
