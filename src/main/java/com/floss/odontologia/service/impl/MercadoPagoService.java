package com.floss.odontologia.service.impl;

import com.floss.odontologia.model.MercadoPagoPayment;
import com.floss.odontologia.model.Pay;
import com.floss.odontologia.repository.IMercadoPagoPaymentRepository;
import com.floss.odontologia.repository.IPayRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MercadoPagoService {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Value("${mercadopago.notification-url}")
    private String notificationUrl;

    private final IPayRepository payRepository;
    private final IMercadoPagoPaymentRepository MPPaymentRepository;

    public MercadoPagoService(IPayRepository payRepository, IMercadoPagoPaymentRepository mpPaymentRepository) {
        this.payRepository = payRepository;
        MPPaymentRepository = mpPaymentRepository;
    }

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
        log.info("MercadoPago configured");
    }

    public String createPaymentPreference(Pay pay) {
        try {
            //create item
            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .title(generateItemTitle(pay))
                    .quantity(1)
                    .unitPrice(pay.getAmount())
                    .currencyId("ARS")
                    .build();

            //back urls
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("https://dentify.com/success-pay")
                    .failure("https://dentify.com/failed-pay")
                    .build();

            //create preference
            String externalRef = "PAY-" + pay.getId_pay();

            PreferenceRequest request = PreferenceRequest.builder()
                    .items(List.of(item))
                    .backUrls(backUrls)
                    .autoReturn("approved")
                    .externalReference(externalRef)
                    .notificationUrl(notificationUrl)
                    .statementDescriptor("Clinica Dental")
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(request);

            //save data
            MercadoPagoPayment mpData = MercadoPagoPayment.builder()
                    .preferenceId(preference.getId())
                    .externalReference(externalRef)
                    .initPoint(preference.getInitPoint())
                    .pay(pay)
                    .build();

            pay.setMercado_pago_data(mpData);
            payRepository.save(pay);

            log.info("Preference created: {} - Link: {}", preference.getId(), preference.getInitPoint());

            return preference.getInitPoint(); //example: https://www.mercadopago.com.ar/checkout/v1/redirect?pref_id=XXX

        } catch (Exception e) {
            log.error("Error creating preference", e);
            throw new RuntimeException("Error generating payment link", e);
        }
    }

    private String generateItemTitle(Pay pay) {

        if ( pay.getAppointment() != null && pay.getTreatment() != null) {
            String product = pay.getTreatment().getProduct() != null
                    ? pay.getTreatment().getProduct().getName_product()
                    : "Treatment";
            return "Treatment - " + product + " - " + pay.getAppointment().getDate();
        }
        return "Dental Treatment";
    }
}
