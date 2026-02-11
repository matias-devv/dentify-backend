package com.floss.odontologia.service.impl;

import com.floss.odontologia.dto.request.appointment.CreateAppointmentRequestDTO;
import com.floss.odontologia.enums.PaymentStatus;
import com.floss.odontologia.model.Appointment;
import com.floss.odontologia.model.Pay;
import com.floss.odontologia.model.Product;
import com.floss.odontologia.model.Treatment;
import com.floss.odontologia.repository.IPayRepository;
import com.floss.odontologia.service.interfaces.IPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayService implements IPayService {

    private final IPayRepository payRepository;

    @Override
    public Pay savePayment(Appointment appointment, Treatment treatment, CreateAppointmentRequestDTO request, Product product) {

        Pay pay = this.buildPay( appointment, treatment, request, product);

        payRepository.save( pay);

        return pay;
    }

    @Override
    public void actualizePaymentStatusToPaidAndSetActualDate(Pay pay) {
        pay.setPayment_status(PaymentStatus.PAID);

        pay.setDate_generation(LocalDateTime.now());

        payRepository.save(pay);
    }

    @Override
    public void actualizePaymentStatusToAwaitingPayment(Pay pay) {
        pay.setPayment_status(PaymentStatus.AWAITING_PAYMENT);

        payRepository.save(pay);
    }

    @Override
    public void actualizePaymentStatusToFailedPayment(Pay pay) {
        pay.setPayment_status(PaymentStatus.FAILED);

        payRepository.save(pay);
    }

    @Override
    public void actualizePaymentStatusToCancelled(Pay pay) {
        pay.setPayment_status(PaymentStatus.CANCELLED);

        payRepository.save(pay);
    }

    private Pay buildPay( Appointment appointment, Treatment treatment, CreateAppointmentRequestDTO request,  Product product) {
        return  Pay.builder()
                .treatment(treatment)
                .appointment(appointment)
                .amount( product.getUnit_price()) // always from the product
                .payment_method( request.paymentMethod())
                .payment_status( PaymentStatus.PENDING)
                .date_generation( LocalDateTime.now())
                .build();
    }
}
