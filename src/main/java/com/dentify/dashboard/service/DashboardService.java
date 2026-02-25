package com.dentify.dashboard.service;

import com.dentify.dashboard.dto.DashboardSummary;
import com.dentify.domain.appointment.enums.AppointmentStatus;
import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.appointment.service.IAppointmentService;
import com.dentify.domain.pay.service.IPayService;
import com.dentify.mapper.AppointmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DashboardService implements IDashboardService {

    private final IAppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;
    private final IPayService payService;

    public DashboardSummary getDashboardSummary() {

        List<AppointmentStatus> statuses = List.of( AppointmentStatus.CANCELLED_BY_SYSTEM, AppointmentStatus.CANCELLED_BY_DENTIST,
                                                    AppointmentStatus.CANCELLED_BY_SECRETARY , AppointmentStatus.CANCELLED_BY_PATIENT);

        BigDecimal dailyIncome = payService.getDailyIncome();
        BigDecimal monthlyIncome = payService.getMonthlyIncome();
        Long appointmentsToday = appointmentService.countAppointmentsTodayExcludingStatuses(statuses);
        Optional<Appointment> appointment = appointmentService.findNextStartTime();

        return appointment.map(appo -> new DashboardSummary(dailyIncome,
                                                                monthlyIncome,
                                                                appointmentsToday,
                                                                appointmentMapper.buildNextAppointment(appo))).orElseGet(() -> new DashboardSummary(dailyIncome,
                                                                                                                                                    monthlyIncome,
                                                                                                                                                    appointmentsToday,
                                                                                                                                                   null));

    }

}
