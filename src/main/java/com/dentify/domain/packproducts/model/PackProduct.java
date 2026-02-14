package com.dentify.domain.packproducts.model;

import com.dentify.domain.treatment.model.Treatment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity @AllArgsConstructor @NoArgsConstructor @Getter @Setter
@Table ( name = "pack_products")
public class PackProduct {

    @Id @GeneratedValue ( strategy = GenerationType.IDENTITY)
    private Long id_pack_product;

    @Column(nullable = false)
    List<Long> products_ids;

    private String name_pack;
    private LocalDate date_creation;
    private BigDecimal total_price;
    private BigDecimal price_without_discount;

    @Column( nullable = true)
    private Integer discount;
    private String description;

    @Column(name = "duration_minutes", nullable = true)
    private Integer duration_minutes;
    private Boolean active;

    @OneToMany ( mappedBy = "pack" )
    private List<Treatment> treatments;

}
