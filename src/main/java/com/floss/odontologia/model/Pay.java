package com.floss.odontologia.model;

import com.floss.odontologia.enums.PaymentMethod;
import com.floss.odontologia.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private PaymentReceipt payment_receipt;

    @OneToMany ( mappedBy = "pay")
    private List<Notification> notifications;

    @ManyToOne ( fetch = FetchType.LAZY)
    @JoinColumn ( name = "id_appointment", nullable = true)
    private Appointment appointment;

    @ManyToOne ( fetch = FetchType.EAGER)
    @JoinColumn ( name = "id_treatment", nullable = false)
    private Treatment treatment;

    @OneToOne ( mappedBy = "pay", cascade = CascadeType.ALL, orphanRemoval = true)
    private MercadoPagoPayment mercado_pago_data;

}
