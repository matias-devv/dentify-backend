package com.floss.odontologia.service.interfaces;

import com.floss.odontologia.model.Speciality;

import java.util.List;

public interface ISpecialityService {

    //read
    public Speciality getSpecialityById(String especiality);
    public List<Speciality> getAllSpecialities();
    //update
    public void editSpeciality(Speciality speciality);
}
