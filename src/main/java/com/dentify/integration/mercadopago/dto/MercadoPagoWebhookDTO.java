package com.dentify.integration.mercadopago.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MercadoPagoWebhookDTO {

    private String action;

    @JsonProperty("api_version")
    private String apiVersion;

    private WebhookData data;

    @JsonProperty("date_created")
    private String dateCreated;

    private String id;

    @JsonProperty("live_mode")
    private Boolean liveMode;

    private String type;

    @JsonProperty("user_id")
    private Long userId;
}