package com.dentify.integration.mercadopago;

import com.dentify.domain.mercadopagopayment.model.MercadoPagoPayment;
import com.dentify.domain.pay.model.Pay;
import com.dentify.domain.mercadopagopayment.repository.IMercadoPagoPaymentRepository;
import com.dentify.domain.pay.repository.IPayRepository;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MercadoPagoService {


    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Value("${mercadopago.notification-url}")
    private String notificationUrl;

    private final IPayRepository payRepository;
    private final IMercadoPagoPaymentRepository mpPaymentRepository;

    private final PreferenceClient preferenceClient;
    private final PaymentClient paymentClient;

    public String createPaymentPreference(Pay pay) {

        try {
            // Create item
            PreferenceItemRequest item = buildItem(pay);
            log.debug("✅ Item creado");

            // Back URLs
            PreferenceBackUrlsRequest backUrls = buildPreferenceBackUrls();
            log.debug("✅ BackURLs configuradas");

            // External reference
            String externalRef = generateExternalReference(pay);
            log.debug("✅ External Reference: {}", externalRef);

            // Build preference request
            PreferenceRequest request = buildPreferenceRequest(item, backUrls, externalRef, notificationUrl);

            log.info("📤 Enviando request a MercadoPago API...");

            if ( notificationUrl != null && !notificationUrl.isBlank() ) {
                log.info("📍 Notification URL: {}", notificationUrl);
            } else {
                log.warn("⚠️ NO SE CONFIGURÓ NOTIFICATION URL - Los webhooks NO llegarán");
            }

            // Create preference
            Preference preference = preferenceClient.create(request);

            log.info("✅ Preferencia creada exitosamente: {}", preference.getId());
            log.info("🔗 Init Point: {}", preference.getInitPoint());

            // Save MercadoPago data
            MercadoPagoPayment mpData = this.saveMercadoPagoPayment(preference, externalRef, pay);
            pay.setMercado_pago_data(mpData);
            payRepository.save(pay);

            return preference.getInitPoint();

        } catch (MPApiException e) {
            log.error("❌ MercadoPago API Error:");
            log.error("  - Status Code: {}", e.getStatusCode());
            log.error("  - Message: {}", e.getMessage());
            log.error("  - Cause: {}", e.getCause());

            if (e.getApiResponse() != null) {
                log.error("  - API Response: {}", e.getApiResponse().getContent());
            }

            throw new RuntimeException("MercadoPago API error: " + e.getMessage(), e);

        } catch (Exception e) {
            log.error("❌ Error general generando link", e);
            throw new RuntimeException("Error generating payment link", e);
        }
    }

    private PreferenceRequest buildPreferenceRequest(PreferenceItemRequest item,
                                                     PreferenceBackUrlsRequest backUrls,
                                                     String externalRef,
                                                     String notificationUrl) {

        PreferenceRequest.PreferenceRequestBuilder builder = PreferenceRequest.builder()
                .items(List.of(item))
                .externalReference(externalRef)
                .statementDescriptor("Clinica Dental");

        // ✅ BackURLs (opcionales pero recomendadas)
        if (backUrls != null) {
            builder.backUrls(backUrls);
        }

        // ✅ NotificationUrl (CRÍTICA para recibir webhooks)
        if (notificationUrl != null && !notificationUrl.isBlank()) {
            builder.notificationUrl(notificationUrl);
        }

        return builder.build();
    }

    private String generateExternalReference(Pay pay) {
        return String.format("PAY_%d_%s", pay.getId_pay(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
    }

    private MercadoPagoPayment saveMercadoPagoPayment(Preference preference, String externalRef, Pay pay) {
        MercadoPagoPayment mpData = buildMercadoPagoPayment(preference.getId(), externalRef,
                preference.getInitPoint(), pay);
        mpPaymentRepository.save(mpData);
        log.debug("MercadoPago data saved for Pay ID: {}", pay.getId_pay());
        return mpData;
    }

    private MercadoPagoPayment buildMercadoPagoPayment(String preferenceId, String externalRef,
                                                       String initPoint, Pay pay) {
        return MercadoPagoPayment.builder()
                .preferenceId(preferenceId)
                .externalReference(externalRef)
                .initPoint(initPoint)
                .pay(pay)
                .build();
    }

    private PreferenceBackUrlsRequest buildPreferenceBackUrls() {
        return PreferenceBackUrlsRequest.builder()
                .success("https://www.mercadopago.com.ar/paid")
                .failure("https://www.mercadopago.com.ar/error")
                .pending("https://www.mercadopago.com.ar/pending")
                .build();
    }

    private PreferenceItemRequest buildItem(Pay pay) {
        return PreferenceItemRequest.builder()
                .title(generateItemTitle(pay))
                .quantity(1)
                .unitPrice(pay.getAmount())
                .currencyId("ARS")
                .build();
    }

    private String generateItemTitle(Pay pay) {
        if (pay.getAppointment() != null && pay.getTreatment() != null) {
            String product = (pay.getTreatment().getProduct() != null)
                    ? pay.getTreatment().getProduct().getName_product()
                    : "Treatment";
            return "Treatment - " + product + " - " + pay.getAppointment().getDate();
        }
        return "Dental Treatment";
    }
}