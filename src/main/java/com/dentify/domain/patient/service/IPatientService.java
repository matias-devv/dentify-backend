package com.dentify.domain.patient.service;

import com.dentify.domain.patient.dto.CreatePatientRequestDTO;
import com.dentify.domain.patient.model.Patient;

public interface IPatientService {

    //create
    public String savePatient(CreatePatientRequestDTO request);

//    //read
//    public PatientDTO getPatient(String dni);

      public Patient findPatientById(Long id_patient);
//    public List<PatientDTO> getPatients();
//
//    public int getTotalOfPatients();
//
//    public List<PatientDTO> getPatientsWithInsurance();
//
//    public List<PatientDTO> getPatientsWithoutInsurance();
//
//    //update
//    public String editPatient(Patient patient);
//
//    //delete
//    public String deletePatient(Long id);
//
//    public PatientDTO setAttributesDto(Patient patient);

}
