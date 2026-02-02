package com.floss.odontologia.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @AllArgsConstructor @NoArgsConstructor @Getter @Setter @Table ( name = "mercado_pago_payments") @Builder
public class MercadoPagoPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String paymentId;

    @Column(nullable = false, unique = true)
    private String preferenceId;

    private String merchantOrderId;

    @Column(nullable = false)
    private String externalReference;

    private Integer installments;
    private String paymentTypeId;

    @OneToOne
    @JoinColumn(name = "pago_id", nullable = false, unique = true)
    private Pay pay;

    @Column(length = 1000)
    private String initPoint;

}
