package com.floss.odontologia.controller;

import com.floss.odontologia.model.Appointment;
import com.floss.odontologia.model.Dentist;
import com.floss.odontologia.model.Patient;
import com.floss.odontologia.service.interfaces.IDentistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dentist")
public class DentistController {

    @Autowired
    private IDentistService iDentistService;

    @PostMapping("/create")
    public String createDentist(@RequestBody Dentist dentist){
        return iDentistService.createDentist(dentist);
    }

    @GetMapping("/find/{id}")
    public Dentist findDentistById(@PathVariable Long id){
        return iDentistService.getDentistById(id);
    }

    @GetMapping("/find-all")
    public List<Dentist> findAllDentist(){
        return iDentistService.getAllDentists();
    }

    @GetMapping("/appointments-dentist")
    public List<Appointment> getAppointmentsByDentist(@RequestBody Dentist dentist){
        return iDentistService.getAppointmentsByDentist(dentist);
    }

    @GetMapping("/patients-dentist")
    public List<Patient> getPatientsByDentist(@RequestBody Dentist dentist){
        return iDentistService.getPatientsByDentist(dentist);
    }

    @PutMapping("/edit")
    public String editDentist(@RequestBody Dentist dentist){
        return iDentistService.editDentist(dentist);
    }
}
