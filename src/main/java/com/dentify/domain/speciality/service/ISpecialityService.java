package com.dentify.domain.speciality.service;

import com.dentify.domain.speciality.dto.SpecialityDTO;
import com.dentify.domain.speciality.model.Speciality;

import java.util.List;

public interface ISpecialityService {

    public String saveSpeciality(SpecialityDTO request);

//    //read
//    public SpecialityDTO getSpecialityDTOByName(String name);
//
//    public List<SpecialityDTO> getAllSpecialities();

    public Speciality getSpecialityEntityById(Long id);

    public String saveAll(List<SpecialityDTO> request);

//
//    //update
//    public String editSpeciality(Speciality speciality);
////
//    public SpecialityDTO convertEntityToDTO(Speciality spe);
}
