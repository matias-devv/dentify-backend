package com.dentify.calendar.dto.response;

import java.math.BigDecimal;

public record ProductResponse(Long id,
                              String name,
                              BigDecimal unit_price,
                              String description) {
}
