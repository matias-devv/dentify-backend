package com.floss.odontologia.controller;

import com.floss.odontologia.dto.response.SpecialityDTO;
import com.floss.odontologia.model.Speciality;
import com.floss.odontologia.service.interfaces.ISpecialityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/speciality")
public class SpecialityController {

    @Autowired
    private ISpecialityService iSpecialityService;

    @GetMapping("/find/{name}")
    public ResponseEntity<?> findById(@PathVariable String name) {
        return ResponseEntity.status(200).body( iSpecialityService.getSpecialityDTOByName(name) );
    }

    @GetMapping("/find-all")
    @ResponseBody
    public List<SpecialityDTO> findAll() {
        return iSpecialityService.getAllSpecialities();
    }

    @PutMapping("/edit")
    public String editSpeciality(@RequestBody Speciality speciality) {
        return iSpecialityService.editSpeciality(speciality);
    }
}
