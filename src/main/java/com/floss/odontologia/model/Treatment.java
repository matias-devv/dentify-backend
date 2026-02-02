package com.floss.odontologia.model;

import com.floss.odontologia.enums.TreatmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity @AllArgsConstructor @NoArgsConstructor @Getter
@Setter @Table ( name = "treatments")
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
    private TreatmentStatus treatment_status = TreatmentStatus.CREATED;

    @Column(nullable = false)
    private LocalDate start_date;

    private LocalDate final_date;

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
