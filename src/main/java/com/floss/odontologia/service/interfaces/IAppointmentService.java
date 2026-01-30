package com.floss.odontologia.service.interfaces;

import com.floss.odontologia.dto.response.AppointmentDTO;
import com.floss.odontologia.model.Appointment;
import com.floss.odontologia.model.AppUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IAppointmentService {

    //create
    public String createAppo(Appointment appointment);

    //read
    public Appointment getAppointmentById(Long id);


}
