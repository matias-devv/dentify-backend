package com.dentify.domain.pay.service;

import com.dentify.domain.appointment.dto.CreateAppointmentRequestDTO;
import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.pay.model.Pay;
import com.dentify.domain.product.model.Product;
import com.dentify.domain.treatment.model.Treatment;

public interface IPayService {

    Pay savePayment(Appointment appointment, Treatment treatment, CreateAppointmentRequestDTO request, Product product);

    void actualizePaymentStatusToPaidAndSetActualDate(Pay pay);

    void actualizePaymentStatusToAwaitingPayment(Pay pay);

    void actualizePaymentStatusToFailedPayment(Pay pay);

    void actualizePaymentStatusToCancelled(Pay pay);

}
