package com.dentify.domain.speciality.model;

import com.dentify.domain.product.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @Entity @AllArgsConstructor @NoArgsConstructor
@Table ( name = "specialities")
public class Speciality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_speciality;
    private String name;

    @OneToMany ( mappedBy = "speciality")
    private List<Product> products;
}
