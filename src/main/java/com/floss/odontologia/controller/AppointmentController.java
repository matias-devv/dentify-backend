package com.floss.odontologia.controller;

import com.floss.odontologia.dto.response.AppointmentDTO;
import com.floss.odontologia.model.Appointment;
import com.floss.odontologia.service.interfaces.IAppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private IAppointmentService iAppointmentService;

    @Operation( summary = "Create appointment")
    @PostMapping("/create")
    public String createAppointment( @RequestBody Appointment appointment){
        return iAppointmentService.createAppo(appointment);
    }

    @Operation(summary = "Get appointment by ID")
    @ApiResponses({
                    @ApiResponse(responseCode = "200", description = "Appointment found"),
                    @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @GetMapping("/find/{id}")
    public ResponseEntity<?> getAppointmentById(    @Parameter(description = "Appointment ID", example = "1")
                                                    @PathVariable Long id){

        Appointment appo = iAppointmentService.getAppointmentById(id);
        return ResponseEntity.status(200).body(appo);
    }

}
