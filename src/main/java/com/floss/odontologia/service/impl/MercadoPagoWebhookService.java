package com.floss.odontologia.service.impl;

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

    @Value("${mercadopago.access-token}")
    private String accessToken;

    private final PaymentProcessorService paymentProcessor;


    // Validate the webhook signature to prevent spoofing attacks.
    // MercadoPago sends an HMAC-SHA256 signature in the x-signature header.
    // Format: "ts=123456789,v1=abc123def456..."
    public boolean validateSignature(String signature, String requestId, Long paymentId) {
        try {
            //extract components of the firm
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
                log.error("Firma mal formada: ts o hash faltante");
                return false;
            }

            // Construir el manifest (string a validar)
            String manifest = String.format("id:%d;request-id:%s;ts:%s", paymentId, requestId, ts);

            log.debug(" Manifest: {}", manifest);

            // Calcular HMAC SHA256
            Mac hmac = Mac.getInstance("HmacSHA256");

            SecretKeySpec secretKey = new SecretKeySpec( accessToken.getBytes(), "HmacSHA256");

            hmac.init(secretKey);

            byte[] hashBytes = hmac.doFinal(manifest.getBytes());

            String calculatedHash = Hex.encodeHexString(hashBytes);

            boolean isValid = calculatedHash.equals(hash);

            if (isValid) {
                log.info("firma valida");
            } else {
                log.error("firma invalida - Hash esperado: {}, Hash recibido: {}",
                        calculatedHash, hash);
            }

            return isValid;

        } catch (Exception e) {
            log.error("error validando firma", e);
            return false;
        }
    }

    /**
     * Process the payment notification
     */
    public void processPayment(Long paymentId) {
        try {
            log.info("procesando pago ID: {}", paymentId);

            // Check the payment on MercadoPago
            PaymentClient client = new PaymentClient();

            Payment payment = client.get(paymentId);

            //Delegate the processing to the specialized service
            paymentProcessor.handlePaymentNotification(payment);

        } catch (Exception e) {
            log.error("Error procesando pago {}", paymentId, e);
            throw new RuntimeException("Error procesando pago", e);
        }
    }
}
