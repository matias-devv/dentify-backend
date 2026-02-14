package com.dentify.domain.receipt.service;

import com.dentify.domain.appointment.model.Appointment;
import com.dentify.domain.mercadopagopayment.model.MercadoPagoPayment;
import com.dentify.domain.pay.model.Pay;
import com.dentify.domain.receipt.model.Receipt;

import java.time.LocalDateTime;
import java.util.List;

public interface IReceiptService {

    public Receipt generateAndSaveReceipt(Pay pay, Appointment appointment, MercadoPagoPayment payMP);


    List<Receipt> findByGenerationDateBefore(LocalDateTime cutoff);

    public void deleteReceipt(Long receiptId);
}
