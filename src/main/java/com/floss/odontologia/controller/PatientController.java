package com.floss.odontologia.controller;

import com.floss.odontologia.model.Patient;
import com.floss.odontologia.service.interfaces.IPatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private IPatientService iPatientService;

    @PostMapping("/create")
    public String createPatient(@RequestBody Patient patient){
        return iPatientService.createPatient(patient);
    }

    @GetMapping("/find/{id}")
    public Patient findPatient(@PathVariable String dni){
        return iPatientService.getPatient(dni);
    }

    @GetMapping("/find-all")
    public List<Patient> findAllPatients(){
        return iPatientService.getPatients();
    }

    @GetMapping("/total-patients")
    public int totalPatients(){
        return iPatientService.getTotalOfPatients();
    }

    @GetMapping("/with-insurance")
    public List<Patient> withInsurance(){
        return iPatientService.getPatientsWithInsurance();
    }

    @GetMapping("/without-insurance")
    public List<Patient> withoutInsurance(){
        return iPatientService.getPatientsWithoutInsurance();
    }

    @PutMapping("/edit")
    public String editPatient(@RequestBody Patient patient){
        return iPatientService.editPatient(patient);
    }

    @DeleteMapping("/delete/{id}")
    public String deletePatient(@PathVariable Long id){
        return iPatientService.deletePatient(id);
    }
}
