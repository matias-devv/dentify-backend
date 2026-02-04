package com.floss.odontologia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Entity @AllArgsConstructor @NoArgsConstructor @Getter @Setter
@Table ( name = "products")
public class Product {

    @Id @GeneratedValue ( strategy = GenerationType.IDENTITY)
    private Long id_product;

    @ManyToOne
    @JoinColumn( name = "id_speciality")
    private Speciality speciality;

    private String name_product;
    private BigDecimal unit_price;
    private String description;
    private Boolean active;

    @OneToMany( mappedBy = "product")
    private List<Agenda> agendas;

    @OneToMany ( mappedBy = "product" )
    private List<Treatment> treatments;
}
