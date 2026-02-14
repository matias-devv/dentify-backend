package com.dentify.domain.speciality.service;

import com.dentify.domain.speciality.dto.SpecialityDTO;
import com.dentify.domain.speciality.model.Speciality;
import com.dentify.domain.speciality.repository.ISpecialityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SpecialityService implements ISpecialityService {

    @Autowired
    private ISpecialityRepository specialityRepository;

    @Override
    public String saveSpeciality(SpecialityDTO request) {

        Speciality speciality = new Speciality();

        speciality.setName( request.name() );

        specialityRepository.save( speciality );
        return "The speciality was saved successfully";
    }

//    @Override
//    public SpecialityDTO getSpecialityDTOByName(String name) {
//
//        List<Speciality> listSpe = iSpecialityRepository.findAll();
//        Speciality speFound = new Speciality();
//
//        for (Speciality spe : listSpe) {
//
//            if ( spe.getName().equalsIgnoreCase(name) ) {
//                speFound = iSpecialityRepository.findById( spe.getId_speciality() ).orElse(null);
//                return this.convertEntityToDTO(speFound);
//            }
//        }
//        return this.convertEntityToDTO(speFound);
//    }

//    @Override
//    public List<SpecialityDTO> getAllSpecialities() {
//        List<Speciality> list = iSpecialityRepository.findAll();
//        List<SpecialityDTO> listDto = new ArrayList<>();
//
//        for (Speciality spe : list) {
//            SpecialityDTO dto = this.convertEntityToDTO(spe);
//            listDto.add(dto);
//        }
//        return listDto;
//    }

    @Override
    public Speciality getSpecialityEntityById(Long id) {
        return specialityRepository.findById(id).orElse(null);
    }

    @Override
    public String saveAll(List<SpecialityDTO> request) {

        List<Speciality> newSpecialities = new ArrayList<>();

        request.forEach(dto -> {

            Speciality speciality = new Speciality();

            speciality.setName(dto.name());

            newSpecialities.add(speciality);
        });
        specialityRepository.saveAll(newSpecialities);

        return "The specialities were saved successfully";
    }


//    @Override
//    public String editSpeciality(Speciality speciality) {
//        iSpecialityRepository.save(speciality);
//        return "The speciality was edited successfully";
//    }
////
//    @Override
//    public SpecialityDTO convertEntityToDTO(Speciality spe) {
//        SpecialityDTO specialityDTO = new SpecialityDTO();
////        if (spe != null) {
////            specialityDTO.setName(spe.getName());
////            specialityDTO.setId(spe.getId_speciality());
////        }
//        return specialityDTO;
//    }
}
