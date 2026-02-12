package com.floss.odontologia.service.interfaces;

import com.floss.odontologia.model.MercadoPagoPayment;
import com.floss.odontologia.model.Pay;
import com.mercadopago.resources.payment.Payment;

import java.util.List;
import java.util.Optional;

public interface IMercadoPagoPaymentService {

    public void updatePayMercadoPagoData(MercadoPagoPayment payMP, Payment payment);

    Optional<MercadoPagoPayment> findByExternalReference(String externalRef);

    Pay findPendingPayWithMercadoPago(List<Pay> pays);
}
