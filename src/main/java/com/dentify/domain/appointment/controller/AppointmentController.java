package com.dentify.domain.appointment.controller;

import com.dentify.calendar.dto.response.FullAppointmentResponse;
import com.dentify.domain.appointment.dto.CreateAppointmentRequestDTO;
import com.dentify.domain.appointment.dto.CreateAppointmentResponseDTO;
import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.appointment.service.IAppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@Slf4j
public class AppointmentController {

    @Autowired
    private IAppointmentService appointmentService;

    @Operation( summary = "Create appointment")
    @PostMapping("/save")
    public ResponseEntity<?> saveAppointment(@RequestBody CreateAppointmentRequestDTO request){
        CreateAppointmentResponseDTO response = appointmentService.saveAppointmentWithPay(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get appointment by ID")
    @ApiResponses({
                    @ApiResponse(responseCode = "200", description = "Appointment found"),
                    @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @GetMapping("/find/{id}")
    public ResponseEntity<FullAppointmentResponse> getAppointmentById(@Parameter(description = "Appointment ID", example = "1")
                                                    @PathVariable Long id){

        FullAppointmentResponse response = appointmentService.getAppointmentById(id);
        return ResponseEntity.status(200).body(response);
    }

}
