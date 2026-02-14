package com.dentify.integration.mercadopago;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
@Slf4j
@RequiredArgsConstructor
public class MercadoPagoWebhookService {

    @Value("${mercadopago.webhook.secret:}")
    private String webhookSecret;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    private final PaymentProcessorService paymentProcessor;


    // Validate the webhook signature to prevent spoofing attacks.
    // MercadoPago sends an HMAC-SHA256 signature in the x-signature header.
    // Format: "ts=123456789,v1=abc123def456..."
    public boolean validateSignature(String signature, String requestId, Long paymentId) {

        if (webhookSecret == null || webhookSecret.isBlank()) {
            log.warn("⚠️ No se configuró webhook secret - validación omitida");
            return true; // Permitir en desarrollo
        }

        try {
            // 1. Extraer ts y hash de la firma
            String[] parts = signature.split(",");
            String ts = null, hash = null;

            for (String part : parts) {
                String[] keyValue = part.trim().split("=", 2);
                if (keyValue.length == 2) {
                    if ("ts".equals(keyValue[0])) {
                        ts = keyValue[1];
                    } else if ("v1".equals(keyValue[0])) {
                        hash = keyValue[1];
                    }
                }
            }

            if (ts == null || hash == null) {
                log.error("❌ Firma malformada: falta ts o v1");
                return false;
            }

            // 2. Construir el manifest según documentación
            // Formato: id:{data.id};request-id:{x-request-id};ts:{ts};
            String manifest = String.format("id:%d;request-id:%s;ts:%s;", paymentId, requestId, ts);

            log.debug("📋 Manifest: {}", manifest);

            // 3. Calcular HMAC SHA256
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(webhookSecret.getBytes(), "HmacSHA256");
            hmac.init(secretKey);
            byte[] hashBytes = hmac.doFinal(manifest.getBytes());
            String calculatedHash = Hex.encodeHexString(hashBytes);

            // 4. Comparar
            boolean isValid = calculatedHash.equals(hash);

            if (isValid) {
                log.info("✅ Firma válida");
            } else {
                log.error("❌ Firma inválida");
                log.error("   Expected: {}", calculatedHash);
                log.error("   Received: {}", hash);
            }

            return isValid;

        } catch (Exception e) {
            log.error("❌ Error validando firma", e);
            return false;
        }
    }

    public void processPayment(Long paymentId) {
        try {
            log.info("🔍 Consultando pago ID: {} en MercadoPago", paymentId);

            PaymentClient client = new PaymentClient();
            Payment payment = client.get(paymentId);

            log.info("✅ Pago obtenido - Status: {}", payment.getStatus());

            paymentProcessor.handlePaymentNotification(payment);

        } catch (Exception e) {
            log.error("❌ Error procesando pago {}", paymentId, e);
            throw new RuntimeException("Error procesando pago", e);
        }
    }
}
