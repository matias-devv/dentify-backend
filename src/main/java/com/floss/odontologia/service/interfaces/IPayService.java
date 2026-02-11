package com.floss.odontologia.service.interfaces;

import com.floss.odontologia.dto.request.appointment.CreateAppointmentRequestDTO;
import com.floss.odontologia.model.Appointment;
import com.floss.odontologia.model.Pay;
import com.floss.odontologia.model.Product;
import com.floss.odontologia.model.Treatment;

import java.util.List;

public interface IPayService {

    Pay savePayment(Appointment appointment, Treatment treatment, CreateAppointmentRequestDTO request, Product product);

    void actualizePaymentStatusToPaidAndSetActualDate(Pay pay);

    void actualizePaymentStatusToAwaitingPayment(Pay pay);

    void actualizePaymentStatusToFailedPayment(Pay pay);

    void actualizePaymentStatusToCancelled(Pay pay);

}
