package com.dentify.dashboard.dto;

import com.dentify.domain.appointment.dto.response.NextAppointment;

import java.math.BigDecimal;

public record DashboardSummary(BigDecimal dailyIncome,
                               BigDecimal monthlyIncome,
                               Long appointmentsToday,
                               NextAppointment nextAppointment) {
}
