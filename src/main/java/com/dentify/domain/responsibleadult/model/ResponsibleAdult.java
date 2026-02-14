package com.dentify.domain.responsibleadult.model;

import com.dentify.domain.responsibleadult.enums.Relation;
import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @AllArgsConstructor
@NoArgsConstructor
@Table(name = "responsible_adults") @Builder
public class ResponsibleAdult {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id_responsible;

    @Column(unique = true)
    private String dni;
    private String name;
    private String surname;
    private String phone_number;
    private String email;

    @Enumerated (EnumType.STRING)
    private Relation relation;

}
