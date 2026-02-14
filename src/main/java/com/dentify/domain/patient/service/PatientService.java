package com.dentify.domain.patient.service;

import com.dentify.domain.patient.dto.CreatePatientRequestDTO;
import com.dentify.domain.responsibleadult.dto.ResponsibleAdultDTO;
import com.dentify.domain.patient.model.Patient;
import com.dentify.domain.patientstat.model.PatientStat;
import com.dentify.domain.responsibleadult.model.ResponsibleAdult;
import com.dentify.domain.patient.repository.IPatientRepository;
import com.dentify.domain.patientstat.service.IPatientStatService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService implements IPatientService {

    private final IPatientRepository patientRepository;

    private final IPatientStatService statService;

    @Override
    @Transactional
    public String savePatient(CreatePatientRequestDTO request) {

        PatientStat stat = statService.createPatientStat();

        if ( request.responsibleAdultList() == null){

            Patient patient = this.buildPatient( request, stat);

            patientRepository.save(patient);

            System.out.println( "ID STAT_ : " + patient.getPatient_stat().getId_stat() );

            return "The patient was registered successfully";

        }

        List<ResponsibleAdult> listAdults = new ArrayList<>();

        request.responsibleAdultList().forEach( adult -> {

            ResponsibleAdult newAdult = this.buildResponsibleAdult(adult);

            listAdults.add(newAdult);
        });

        Patient patient = this.buildPatient(request, stat);

        patient.setResponsibleAdultList(listAdults);

        patientRepository.save(patient);

        return "The patient was registered successfully";
    }

    @Override
    public Patient findPatientById(Long id_patient) {
        return patientRepository.findById( id_patient ).orElseThrow( () -> new RuntimeException("Patient not found"));
    }

    private ResponsibleAdult buildResponsibleAdult(ResponsibleAdultDTO adult) {
        return ResponsibleAdult.builder()
                .dni( adult.dni())
                .name( adult.name())
                .surname( adult.surname())
                .phone_number( adult.phone_number())
                .email( adult.email())
                .relation( adult.relation())
                .build();
    }

    private Patient buildPatient(CreatePatientRequestDTO request, PatientStat stat) {
        return Patient.builder()
                .dni(request.dni())
                .name(request.name())
                .surname(request.surname())
                .age(request.age())
                .date_of_birth(request.date_of_birth())
                .insurance(request.insurance())
                .coverageType(request.coverageType())
                .phone_number(request.phone_number())
                .email(request.email())
                .patient_stat(stat)
                .build();
    }
//
//    @Override
//    public PatientDTO getPatient(String dni) {
//
//        List<Patient> listPatients = iPatientRepository.findAll();
//
//        for (Patient pa : listPatients) {
//            //If the dni is the same -> return patient
//            if ( pa.getDni().equals(dni)) {
//                Patient patient = iPatientRepository.findById(pa.getId_patient()).orElse(null);
//                return this.setAttributesDto(patient);
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public List<PatientDTO> getPatients() {
//        List<Patient> list = iPatientRepository.findAll();
//        List<PatientDTO> listPatientsDto = new ArrayList<>();
//
//        for( Patient pa : list){
//            //for each patient -> dto
//            PatientDTO patientDto = this.setAttributesDto(pa);
//            //dto -> list dto
//            listPatientsDto.add(patientDto);
//        }
//        return listPatientsDto;
//    }
//
//    @Override
//    public int getTotalOfPatients() {
//        return iPatientRepository.findAll().size();
//    }
//
//    @Override
//    public List<PatientDTO> getPatientsWithInsurance() {
//        List<PatientDTO> allPatients = this.getPatients();
//        List<PatientDTO> listWithInsurance = new ArrayList<>();
//
//        for (PatientDTO patientDto : allPatients) {
//            if( patientDto.getInsurance() == true){
//                listWithInsurance.add(patientDto);
//            }
//        }
//        return listWithInsurance;
//    }
//
//    @Override
//    public List<PatientDTO> getPatientsWithoutInsurance() {
//        List<PatientDTO> allPatients = this.getPatients();
//        List<PatientDTO> listWithoutInsurance = new ArrayList<>();
//
//        for (PatientDTO patientDto : allPatients) {
//            if( patientDto.getInsurance() == false){
//                listWithoutInsurance.add(patientDto);
//            }
//        }
//        return listWithoutInsurance;
//    }
//
//    @Override
//    public String editPatient(Patient patient) {
//        iPatientRepository.save(patient);
//        return "The patient was edited successfully";
//    }
//
//    @Override
//    public String deletePatient(Long id) {
//        try{
//            iPatientRepository.deleteById(id);
//            return "The patient was deleted successfully";
//        }catch (Exception e) {
//            return "The patient does not exist in the database";
//        }
//    }
//
//
//    @Override
//    public PatientDTO setAttributesDto(Patient patient) {
//        PatientDTO dto = new PatientDTO();
//        dto.setId_patient(patient.getId_patient());
//        dto.setName(patient.getName());
//        dto.setSurname(patient.getSurname());
//        dto.setDni(patient.getDni());
//        dto.setAge(patient.getAge());
//        dto.setDate_of_birth(patient.getDate_of_birth());
//        dto.setInsurance(patient.getInsurance());
//        dto.setPatient_condition(patient.getPatient_condition());
//        dto.setRoutine(patient.getRoutine());
//        dto.setTreatment_type(patient.getTreatment_type());
//        return dto;
//    }
}
