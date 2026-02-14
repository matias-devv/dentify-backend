package com.dentify.domain.treatment.model;

import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.treatment.enums.TreatmentStatus;
import com.dentify.domain.packproducts.model.PackProduct;
import com.dentify.domain.patient.model.Patient;
import com.dentify.domain.pay.model.Pay;
import com.dentify.domain.product.model.Product;
import com.dentify.domain.user.model.AppUser;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity @AllArgsConstructor @NoArgsConstructor @Getter
@Setter @Table ( name = "treatments") @Builder
public class Treatment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_treatment;

    @Column(nullable = false)
    private BigDecimal base_price;

    private BigDecimal discount = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal final_price;

    @Column(nullable = false)
    private BigDecimal outstanding_balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TreatmentStatus treatmentStatus = TreatmentStatus.CREATED;

    @Column(nullable = false)
    private LocalDateTime start_date;

    private LocalDateTime final_date;

    //n treatments -> one app_user
    @ManyToOne ( fetch = FetchType.LAZY)
    @JoinColumn ( name = "id_app_user", nullable = false)
    private AppUser app_user;

    //n treatments -> one product
    @ManyToOne ( fetch = FetchType.LAZY)
    @JoinColumn ( name = "id_product", nullable = true)
    private Product product;

    //n treatments -> one pack
    @ManyToOne ( fetch = FetchType.LAZY)
    @JoinColumn ( name = "id_pack", nullable = true)
    private PackProduct pack;

    //n treatments -> one patient
    @ManyToOne ( fetch = FetchType.LAZY)
    @JoinColumn ( name = "id_patient", nullable = false)
    private Patient patient;

    //one treatment -> n pagos
    @OneToMany ( mappedBy = "treatment", cascade = CascadeType.ALL)
    private List<Pay> pays;

    //one treatment -> n appointments
    @OneToMany ( mappedBy = "treatment")
    private List<Appointment> appointments;
}
