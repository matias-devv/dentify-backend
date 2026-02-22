package com.dentify.dashboard.controller;

import com.dentify.dashboard.dto.DashboardSummary;
import com.dentify.dashboard.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final IDashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummary getDashboardSummary() {
        return dashboardService.getDashboardSummary();
    }

}
