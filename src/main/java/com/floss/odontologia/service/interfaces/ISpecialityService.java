package com.floss.odontologia.service.interfaces;

import com.floss.odontologia.dto.request.SpecialityDTO;
import com.floss.odontologia.model.Speciality;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;

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
