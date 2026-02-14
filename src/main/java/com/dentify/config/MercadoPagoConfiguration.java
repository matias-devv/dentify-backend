package com.dentify.config;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

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
            log.info("🚀 Iniciando configuración de MercadoPago SDK...");

            //validate if exists
            if (accessToken == null || accessToken.isBlank()) {
                throw new RuntimeException("MercadoPago Access Token is required");
            }

            //verify type of token
            String tokenType = accessToken.startsWith("TEST-") ? "TEST" : "PRODUCTION";
            log.info("🔑 Token type: {}", tokenType);
            log.info("🔑 Token preview: {}***", accessToken.substring(0, Math.min(15, accessToken.length())));

            // Set the access token globally for all MercadoPago API calls
            MercadoPagoConfig.setAccessToken(accessToken);

            log.info("✅ MercadoPago SDK configured successfully");

        } catch (Exception e) {
            log.error("💥 Error configurando MercadoPago SDK", e);
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