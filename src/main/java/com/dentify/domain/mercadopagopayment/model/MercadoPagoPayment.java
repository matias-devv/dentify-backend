package com.dentify.domain.mercadopagopayment.model;

import com.dentify.domain.pay.model.Pay;
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

    @Column(length = 1000)
    private String initPoint;

    //fields for debug
    private String status;                  // approved, pending, rejected
    private String statusDetail;            // cc_rejected_bad_filled_card_number
    private BigDecimal transactionAmount;
    private String payerEmail;
    private LocalDateTime dateApproved;

    @OneToOne
    @JoinColumn(name = "pago_id", nullable = false, unique = true)
    private Pay pay;

}
