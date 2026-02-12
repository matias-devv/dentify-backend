package com.floss.odontologia.service.interfaces;

import com.floss.odontologia.model.Appointment;
import com.floss.odontologia.model.MercadoPagoPayment;
import com.floss.odontologia.model.Pay;
import com.floss.odontologia.model.Receipt;

import java.time.LocalDateTime;
import java.util.List;

public interface IReceiptService {

    public Receipt generateAndSaveReceipt(Pay pay, Appointment appointment, MercadoPagoPayment payMP);


    List<Receipt> findByGenerationDateBefore(LocalDateTime cutoff);

    public void deleteReceipt(Long receiptId);
}
