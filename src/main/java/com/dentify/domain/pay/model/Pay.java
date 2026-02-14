package com.dentify.domain.pay.model;

import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.pay.enums.PaymentMethod;
import com.dentify.domain.pay.enums.PaymentStatus;
import com.dentify.domain.mercadopagopayment.model.MercadoPagoPayment;
import com.dentify.domain.receipt.model.Receipt;
import com.dentify.domain.treatment.model.Treatment;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Table ( name = "pays") @Builder
public class Pay {

    @Id @GeneratedValue ( strategy = GenerationType.IDENTITY)
    private Long id_pay;

    private BigDecimal amount;

    @Enumerated ( EnumType.STRING )
    private PaymentMethod payment_method;

    @Enumerated ( EnumType.STRING)
    private PaymentStatus payment_status = PaymentStatus.PENDING;

    private LocalDateTime date_generation;

    private Integer total_installments;
    private Integer pay_installments = 0;

    @OneToOne ( mappedBy = "pay")
    private Receipt payment_receipt;

    @ManyToOne ( fetch = FetchType.LAZY)
    @JoinColumn ( name = "id_appointment", nullable = true)
    private Appointment appointment;

    @ManyToOne ( fetch = FetchType.EAGER)
    @JoinColumn ( name = "id_treatment", nullable = false)
    private Treatment treatment;

    @OneToOne ( mappedBy = "pay", cascade = CascadeType.ALL, orphanRemoval = true)
    private MercadoPagoPayment mercado_pago_data;

}
