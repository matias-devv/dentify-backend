package com.floss.odontologia.controller;

import com.floss.odontologia.model.Appointment;
import com.floss.odontologia.model.Dentist;
import com.floss.odontologia.service.interfaces.IAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private IAppointmentService iAppointmentService;

    @PostMapping("/create")
    public String createAppointment(@RequestBody Appointment appointment){
        return iAppointmentService.createAppo(appointment);
    }

    @GetMapping("/find/{id}")
    public Appointment getAppointmentById(@PathVariable Long id){
        return iAppointmentService.getAppointmentById(id);
    }

    @GetMapping("/find-all")
    public List<Appointment> getAllAppointments(){
        return iAppointmentService.getAllAppointments();
    }

    @GetMapping("/appointments-today")
    public int getAppointmentNumberToday(@RequestBody Dentist dentist){
        return iAppointmentService.getAppointmentNumberToday(dentist);
    }

    @GetMapping("/hours/{date}/{selectedDay}")
    public List<LocalTime> getHoursOfDentist(
                                             @PathVariable LocalDate date,
                                             @RequestBody Dentist dentist,
                                             @PathVariable String selectedDay){
        return iAppointmentService.getHoursOfDentist(date, dentist, selectedDay);
    }

    @PutMapping("/edit")
    public String editAppointment(@RequestBody Appointment appointment){
        return iAppointmentService.editAppo(appointment);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteAppointment(@PathVariable Long id){
        return iAppointmentService.deleteAppo(id);
    }
}
