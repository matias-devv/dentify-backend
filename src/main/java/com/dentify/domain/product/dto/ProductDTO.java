package com.dentify.domain.product.dto;

import java.math.BigDecimal;

public record ProductDTO(Long id_speciality,
                         String name_product,
                         BigDecimal unit_price,
                         String description,
                         Integer duration_minutes,
                         Boolean activo) {
}
