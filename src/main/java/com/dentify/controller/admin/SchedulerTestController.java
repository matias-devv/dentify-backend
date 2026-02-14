package com.dentify.controller.admin;

import com.dentify.scheduler.AppointmentScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/scheduler")
@RequiredArgsConstructor
public class SchedulerTestController {

    private final AppointmentScheduler scheduler;
//
//    // Solo para desarrollo - QUITAR EN PRODUCCIÓN
//    @PostMapping("/test-payment-expiration")
//    public ResponseEntity<?> testPaymentExpiration() {
//        scheduler.verifyExpiredPays();
//        return ResponseEntity.ok("Ejecutado");
//    }
//
//    @PostMapping("/test-no-shows")
//    public ResponseEntity<?> testNoShows() {
//        scheduler.markNoShows();
//        return ResponseEntity.ok("Ejecutado");
//    }
//
}