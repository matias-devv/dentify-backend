package com.floss.odontologia.service.interfaces;

import com.floss.odontologia.dto.response.SpecialityDTO;
import com.floss.odontologia.model.Speciality;

import java.util.List;

public interface ISpecialityService {

    //read
    public SpecialityDTO getSpecialityDTOByName(String name);

    public Speciality getSpecialityByName(String name);

    public List<SpecialityDTO> getAllSpecialities();

    //update
    public String editSpeciality(Speciality speciality);

    public SpecialityDTO convertEntityToDTO(Speciality spe);
}
