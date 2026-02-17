package com.dentify.calendar.dto.response;

import com.dentify.domain.treatment.enums.TreatmentStatus;

import java.math.BigDecimal;

public record TreatmentResponse(Long id,
                                TreatmentStatus status,
                                BigDecimal pendingBalance) {
}
