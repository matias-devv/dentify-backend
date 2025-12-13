package com.floss.odontologia.service.impl;

import com.floss.odontologia.dto.response.SpecialityDTO;
import com.floss.odontologia.model.Speciality;
import com.floss.odontologia.repository.ISpecialityRepository;
import com.floss.odontologia.service.interfaces.ISpecialityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SpecialityService implements ISpecialityService {

    @Autowired
    private ISpecialityRepository iSpecialityRepository;

    @Override
    public SpecialityDTO getSpecialityDTOByName(String name) {

        List<Speciality> listSpe = iSpecialityRepository.findAll();
        Speciality speFound = new Speciality();

        for (Speciality spe : listSpe) {

            if ( spe.getName().equalsIgnoreCase(name) ) {
                speFound = iSpecialityRepository.findById( spe.getId_speciality() ).orElse(null);
                return this.convertEntityToDTO(speFound);
            }
        }
        return this.convertEntityToDTO(speFound);
    }

    @Override
    public Speciality getSpecialityByName(String name) {

        List<Speciality> listSpe = iSpecialityRepository.findAll();
        Speciality speFound = new Speciality();

        for (Speciality spe : listSpe) {

            if ( spe.getName().equalsIgnoreCase(name) ) {
                speFound = iSpecialityRepository.findById( spe.getId_speciality() ).orElse(null);
                return speFound;
            }
        }
        return speFound;
    }

    @Override
    public List<SpecialityDTO> getAllSpecialities() {
        List<Speciality> list = iSpecialityRepository.findAll();
        List<SpecialityDTO> listDto = new ArrayList<>();

        for (Speciality spe : list) {
            SpecialityDTO dto = this.convertEntityToDTO(spe);
            listDto.add(dto);
        }
        return listDto;
    }

    @Override
    public String editSpeciality(Speciality speciality) {
        iSpecialityRepository.save(speciality);
        return "The speciality was edited successfully";
    }

    @Override
    public SpecialityDTO convertEntityToDTO(Speciality spe) {
        SpecialityDTO specialityDTO = new SpecialityDTO();
        if (spe != null) {
            specialityDTO.setName(spe.getName());
            specialityDTO.setId(spe.getId_speciality());
        }
        return specialityDTO;
    }
}
