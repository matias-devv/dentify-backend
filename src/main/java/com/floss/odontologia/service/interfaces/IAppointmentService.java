package com.floss.odontologia.service.interfaces;

import com.floss.odontologia.dto.request.appointment.CreateAppointmentRequestDTO;
import com.floss.odontologia.dto.response.appointment.CreateAppointmentResponseDTO;
import com.floss.odontologia.model.Appointment;

public interface IAppointmentService {

    //read
    public Appointment getAppointmentById(Long id);

    public CreateAppointmentResponseDTO saveAppointmentWithPay(CreateAppointmentRequestDTO request);
}
