package com.floss.odontologia.service.interfaces;

import com.floss.odontologia.model.MercadoPagoPayment;
import com.mercadopago.resources.payment.Payment;

public interface IMercadoPagoPayment {

    public void updatePayMercadoPagoData(MercadoPagoPayment payMP, Payment payment);
}
