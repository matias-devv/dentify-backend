package com.dentify.config;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * MercadoPago SDK Configuration
 * Initializes the MercadoPago client with access token
 */
@Configuration
@Slf4j
public class MercadoPagoConfiguration {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Value("${mercadopago.public-key:}")
    private String publicKey;

    /**
     * Initialize MercadoPago SDK globally
     * This runs once when the application starts
     */
    @PostConstruct
    public void init() {
        try {
            // Set the access token globally for all MercadoPago API calls
            MercadoPagoConfig.setAccessToken(accessToken);

            log.info("MercadoPago SDK configured successfully");
            log.info("Access Token: {}***", accessToken.substring(0, Math.min(10, accessToken.length())));

        } catch (Exception e) {
            log.error("Error configuring MercadoPago SDK", e);
            throw new RuntimeException("Failed to configure MercadoPago SDK", e);
        }
    }

    /**
     * Bean for PaymentClient (used for fetching/processing payments)
     * This allows dependency injection and easier mocking in tests
     */
    @Bean
    public PaymentClient paymentClient() {
        log.debug("Creating PaymentClient bean");
        return new PaymentClient();
    }

    /**
     * Bean for PreferenceClient (used for creating payment links)
     */
    @Bean
    public PreferenceClient preferenceClient() {
        log.debug("Creating PreferenceClient bean");
        return new PreferenceClient();
    }
}